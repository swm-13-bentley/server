package com.schedch.mvp.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import com.schedch.mvp.adapter.TimeAdapter;
import com.schedch.mvp.config.GoogleConfigUtils;
import com.schedch.mvp.dto.CalendarResponse;
import com.schedch.mvp.dto.CalendarScheduleDto;
import com.schedch.mvp.dto.GoogleLoginRequest;
import com.schedch.mvp.dto.GoogleLoginResponse;
import com.schedch.mvp.model.GToken;
import com.schedch.mvp.model.Room;
import com.schedch.mvp.model.RoomDate;
import com.schedch.mvp.repository.GTokenRepository;
import com.schedch.mvp.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional
public class GoogleCalendarService {

    private final GoogleConfigUtils googleConfigUtils;
    private final GTokenRepository gTokenRepository;
    private final RoomService roomService;
    private final TimeAdapter timeAdapter;

    public void save(GToken gToken) {
        GToken save = gTokenRepository.save(gToken);
    }
    public List<CalendarResponse> getSchedulesInRoomRange(String roomUuid, String state) throws GeneralSecurityException, IOException {
        Optional<GToken> gTokenOptional = gTokenRepository.findByState(state);
        GToken gToken = gTokenOptional.orElseThrow(
                () -> new NoSuchElementException(String.format("GToken for state: %s does not exist", state))
        );

        TokenResponse tokenResponse = new TokenResponse()
                .setAccessToken(gToken.getAccessToken())
                .setRefreshToken(gToken.getRefreshToken())
                .setTokenType(gToken.getTokenType())
                .setExpiresInSeconds(gToken.getExpiresIn())
                .setScope(gToken.getScope());

        Calendar googleCalendar = getGoogleCalendar(tokenResponse);

        Room room = roomService.getRoom(roomUuid);
        List<LocalDate> roomDates = room.getRoomDates().stream().map(RoomDate::getScheduledDate).collect(Collectors.toList());

        CalendarList calendarList = getCalendarList(googleCalendar);
        Colors colors = googleCalendar.colors().get().execute();

        DateTime startDateTime = new DateTime(1000 * roomDates.get(0).toEpochSecond(LocalTime.of(0, 0, 0), ZoneOffset.of("+9")));
        DateTime endDateTime = new DateTime(1000 * roomDates.get(roomDates.size()-1).toEpochSecond(LocalTime.of(23, 59, 59), ZoneOffset.of("+9")));

        List<CalendarResponse> calendarResponseList = new ArrayList<>();
        calendarList.getItems().stream()
                .forEach(calendarListEntry -> {
                    HashMap<LocalDate, HashSet<Integer>> map = new HashMap<>();

                    String summary = calendarListEntry.getSummary();
                    String calendarId = calendarListEntry.getId();
                    String colorId = calendarListEntry.getColorId();
                    String colorCode = colors.getCalendar().get(colorId).getBackground();
                    CalendarResponse calendarResponse = new CalendarResponse(summary, calendarId, colorCode);

                    Events events = null;
                    try {
                        events = googleCalendar.events().list(calendarId)
                                .setTimeMin(startDateTime)
                                .setTimeMax(endDateTime)
                                .setOrderBy("startTime")
                                .setSingleEvents(true)
                                .execute();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    List<Event> eventList = events.getItems();

                    for (Event event : eventList) {//매 이벤트마다
                        DateTime start = event.getStart().getDateTime();
                        if(start == null) {//하루 종일로 설정된 이벤트임
                            continue;
                        }
                        DateTime end = event.getEnd().getDateTime();

                        int roomStartTimeBlock = localTime2TimeBlockInteger(room.getStartTime());
                        int roomEndTimeBlock = localTime2TimeBlockInteger(room.getEndTime());
                        addToMap(map, start, end, roomStartTimeBlock, roomEndTimeBlock);

                    }

                    HashSet<LocalDate> roomDatesSet = new HashSet<>(roomDates);

                    for (Map.Entry<LocalDate, HashSet<Integer>> mapEntry : map.entrySet()) {
                        LocalDate key = mapEntry.getKey();
                        HashSet<Integer> value = mapEntry.getValue();

                        if(roomDatesSet.contains(key) == false) continue;

                        CalendarScheduleDto calendarScheduleDto = new CalendarScheduleDto(key);
                        List<Integer> scheduledTimeList = calendarScheduleDto.getScheduledTimeList();
                        value.stream().sorted().forEach(block -> scheduledTimeList.add(block));
                        calendarResponse.getEvents().add(calendarScheduleDto);
                    }

                    calendarResponseList.add(calendarResponse);

                });

        return calendarResponseList;
    }

    private CalendarList getCalendarList(Calendar googleCalendar) throws IOException {
        return googleCalendar.calendarList().list().execute();
    }

    private void addToMap(HashMap<LocalDate, HashSet<Integer>> map, DateTime start, DateTime end, int roomStartTimeBlockInt, int roomEndTimeBlockInt) {
        LocalDate startDate = timeAdapter.dateTime2LocalDate(start);
        LocalTime startTime = timeAdapter.dateTime2LocalTime(start);

        LocalDate endDate = timeAdapter.dateTime2LocalDate(end);
        LocalTime endTime = timeAdapter.dateTime2LocalTime(end);

        int startTimeBlockInt = timeAdapter.localTime2TimeBlockInt(startTime);
        int endTimeBlockInt = timeAdapter.localTime2TimeBlockInt(endTime);

        startTimeBlockInt = Math.max(startTimeBlockInt, roomStartTimeBlockInt);
        endTimeBlockInt = Math.min(endTimeBlockInt, roomEndTimeBlockInt);

        if (startDate.isEqual(endDate)) {

            HashSet<Integer> set = map.getOrDefault(startDate, new HashSet<>());
            for (int i = startTimeBlockInt; i <= endTimeBlockInt; i++) {
                set.add(i);
            }
            map.put(startDate, set);

        } else {
            HashSet<Integer> startSet = map.getOrDefault(startDate, new HashSet<>());
            for (int i = roomStartTimeBlockInt; i <= roomEndTimeBlockInt; i++) {
                startSet.add(i);
            }
            map.put(startDate, startSet);

            HashSet<Integer> endSet = map.getOrDefault(endDate, new HashSet<>());
            for (int i = roomStartTimeBlockInt; i <= roomEndTimeBlockInt; i++) {
                endSet.add(i);
            }
            map.put(endDate, endSet);

            int plus = 1;
            while(startDate.plusDays(plus).isBefore(endDate)) {
                HashSet<Integer> fullSet = new HashSet<>(IntStream.range(0, 48 + 1).boxed().collect(Collectors.toList()));
                map.put(startDate.plusDays(plus), fullSet);
                plus++;
            }
        }

    }

    private int localTime2TimeBlockInteger(LocalTime time) {
        int block = (int) (time.getHour() * (60/30)
                + Math.floor(time.getMinute() / 30));

        return block;
    }

    public TokenResponse getTokenResponse(String code) {
        RestTemplate restTemplate = new RestTemplate();
        GoogleLoginRequest requestParams = GoogleLoginRequest.builder()
                .clientId(googleConfigUtils.getGoogleClientId())
                .clientSecret(googleConfigUtils.getGoogleSecret())
                .code(code)
                .redirectUri(googleConfigUtils.getGoogleRedirectUrl())
                .grantType("authorization_code")
                .build();

        try {
            // Http Header 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<GoogleLoginRequest> httpRequestEntity = new HttpEntity<>(requestParams, headers);
            ResponseEntity<String> apiResponseJson = restTemplate.postForEntity(googleConfigUtils.getGoogleAuthUrl() + "/token", httpRequestEntity, String.class);

            // ObjectMapper를 통해 String to Object로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // NULL이 아닌 값만 응답받기(NULL인 경우는 생략)
            GoogleLoginResponse googleLoginResponse = objectMapper.readValue(apiResponseJson.getBody(), new TypeReference<GoogleLoginResponse>() {});

            TokenResponse tokenResponse = new TokenResponse()
                    .setAccessToken(googleLoginResponse.getAccessToken())
                    .setRefreshToken(googleLoginResponse.getRefreshToken())
                    .setTokenType(googleLoginResponse.getTokenType())
                    .setExpiresInSeconds(Long.parseLong(googleLoginResponse.getExpiresIn()))
                    .setScope(googleLoginResponse.getScope());

            return tokenResponse;

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Calendar getGoogleCalendar(TokenResponse tokenResponse) throws GeneralSecurityException, IOException {
        NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        String credentialsFilePath = googleConfigUtils.getCREDENTIALS_FILE_PATH();
        JsonFactory json_factory = googleConfigUtils.getJSON_FACTORY();
        List<String> scopeList = googleConfigUtils.getScopeList();
        String tokens_directory_path = googleConfigUtils.getTOKENS_DIRECTORY_PATH();

        InputStream in = GoogleCalendarService.class.getResourceAsStream(credentialsFilePath);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(json_factory, new InputStreamReader(in));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, json_factory, clientSecrets, scopeList)
//                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(tokens_directory_path)))
                .setAccessType("offline")
                .build();

        Credential credential = flow.createAndStoreCredential(tokenResponse, "userId");

        return new Calendar.Builder(HTTP_TRANSPORT, json_factory, credential)
                .setApplicationName("Mannatime")
                .build();

    }
}
