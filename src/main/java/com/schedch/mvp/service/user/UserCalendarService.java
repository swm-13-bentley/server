package com.schedch.mvp.service.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.schedch.mvp.adapter.TimeAdapter;
import com.schedch.mvp.config.ErrorMessage;
import com.schedch.mvp.dto.oauth.GoogleLoginDto;
import com.schedch.mvp.dto.user.SubCalendarReq;
import com.schedch.mvp.dto.user.UserCalendarLoadPerDay;
import com.schedch.mvp.dto.user.UserCalendarReq;
import com.schedch.mvp.exception.CalendarLoadException;
import com.schedch.mvp.model.Room;
import com.schedch.mvp.model.SubCalendar;
import com.schedch.mvp.model.User;
import com.schedch.mvp.model.UserCalendar;
import com.schedch.mvp.repository.UserCalendarRepository;
import com.schedch.mvp.service.calendar.CalendarService;
import com.schedch.mvp.service.oauth.GoogleProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.security.auth.login.FailedLoginException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserCalendarService {

    private final GoogleProfileService googleProfileService;
    private final CalendarService calendarService;
    private final UserService userService;
    private final UserCalendarRepository userCalendarRepository;

    public void addCalendar(Long userId, String authCode) throws FailedLoginException, JsonProcessingException, CalendarLoadException {

        GoogleLoginDto googleLoginDto = googleProfileService.getGoogleProfileByType(authCode, "addCalendar");
        String calendarEmail = googleLoginDto.getEmail();
        User user = userService.getUserById(userId);

        Optional<UserCalendar> userCalendarOptional = userCalendarRepository.findByCalendarEmailAndUser(calendarEmail, user);
        if (userCalendarOptional.isPresent()) {
            log.info("E: addCalendar() /  User added already existing calendar / userCalendarId = {}", userCalendarOptional.get().getId());
            return;
        }

        UserCalendar userCalendar = googleLoginDto.toUserCalendar();
        user.addUserCalendar(userCalendar);
    }

    public UserCalendar addCalendarToUser(GoogleLoginDto googleLoginDto, User user) throws CalendarLoadException {
        List<UserCalendar> userCalendarList = user.getUserCalendarList();
        for (UserCalendar userCalendar : userCalendarList) {
            if(userCalendar.getCalendarEmail() == googleLoginDto.getEmail()) {
                return userCalendar;
            }
        }

        try {
            //토큰으로 캘린더 불러오기
            TokenResponse tokenResponse = googleLoginDto.toTokenResponse();
            Calendar gCal = calendarService.getGoogleCalendarByTokenResponse(tokenResponse);

            //캘린더로 calendarList 불러오기
            CalendarList gCalendarList = gCal.calendarList().list().execute();

            //UserCalendar 객체 생성 후 추가
            UserCalendar userCalendar = googleLoginDto.toUserCalendar();
            user.addUserCalendar(userCalendar);
            if(user.getUserCalendarList().size() == 1) { //첫 캘린더인 경우 메인 캘린더로 추가
                userCalendar.setMainCalendar(true);
            }

            //SubCalendar 객체 생성 후 추가
            List<CalendarListEntry> items = gCalendarList.getItems();
            items.stream().forEach(calendarListEntry -> {
                String subCalendarName = calendarListEntry.getSummary();
                String gCalId = calendarListEntry.getId();
                Boolean selected = calendarListEntry.getSelected();
                if(selected == null) selected = false;
                SubCalendar subCalendar = new SubCalendar(subCalendarName, selected, gCalId);
                userCalendar.addSubCalendar(subCalendar);
            });

            return userCalendar;

        } catch (GeneralSecurityException e) {
            log.error("F: addCalendarToUser() / GeneralSecurityException / userId = {}, errorMsg = {}", user.getId(), e.getMessage());
            throw new CalendarLoadException(e.getMessage());
        } catch (IOException e) {
            log.error("F: addCalendarToUser() / IOException / userId = {}, errorMsg = {}", user.getId(), e.getMessage());
            throw new CalendarLoadException(e.getMessage());
        } catch (Exception e) {
            log.error("F: addCalendarToUser() / Google calendar load error / userId = {}, errorMsg = {}", user.getId(), e.getMessage());
            throw new CalendarLoadException(e.getMessage());
        }
    }

    public List<UserCalendar> getAllUserCalendar(String userEmail) {
        User user = userService.getUserByEmail(userEmail);
        List<UserCalendar> userCalendarList = userCalendarRepository.findAllByUserJoinFetchSubCalendar(user);

        return userCalendarList;
    }

    public UserCalendarLoadPerDay loadCalendarEvents(String userEmail, Room room) throws IllegalAccessException {
        User user = userService.getUserByEmail(userEmail);
        if (user.getScope().split(" ").length != 4) {
            //권한 부족함
            throw new IllegalAccessException("Calendar access not granted");
        }

        Optional<UserCalendar> mainUserCalendarOptional = userCalendarRepository.findMainCalendarByUserJoinFetchSubCalendar(user);

        if(mainUserCalendarOptional.isEmpty()) {
            throw new NoSuchElementException(ErrorMessage.cannotFindMainCalendarError());
        }

        //get selected subCalendar ids as set
        UserCalendar mainUserCalendar = mainUserCalendarOptional.get();
        Set<String> selectedSubCalSet = mainUserCalendar.getSelectedSubCalIdSet();

        //create tokenResponse
        TokenResponse tokenResponse = new TokenResponse()
                .setAccessToken(mainUserCalendar.getCalendarAccessToken())
                .setRefreshToken(mainUserCalendar.getCalendarRefreshToken());

        //Save items
        List<Event> eventList = new ArrayList<>();
        DateTime startDateTime = TimeAdapter.localDateAndTime2DateTime(room.getStartLocalDate(), room.getStartTime(), "+9");
        DateTime endDateTime = TimeAdapter.localDateAndTime2DateTime(room.getEndLocalDate(), room.getEndTime(), "+9");

        log.info("start google transaction");
        try {
            Calendar gCal = calendarService.getGoogleCalendarByTokenResponse(tokenResponse);
            List<CalendarListEntry> calendarsToShow = gCal.calendarList().list().execute().getItems().stream()
                    .filter(item -> selectedSubCalSet.contains(item.getId()))
                    .collect(Collectors.toList());

            for (CalendarListEntry calendarListEntry : calendarsToShow) {
                Events events = gCal.events().list(calendarListEntry.getId())
                        .setTimeMin(startDateTime) //lower bound of end datetime
                        .setTimeMax(endDateTime) //upper bound of start datetime
                        .setOrderBy("startTime")
                        .setSingleEvents(true)
                        .execute();

                List<Event> eventsItems = events.getItems(); // 방 날짜 내, 방 시작 시간 내의 event items

                for (Event eventItem : eventsItems) {
                    if (eventItem.getStart().getDateTime() == null) { //하루 종일로 설정 된 이벤트
                        continue;
                    }

                    eventList.add(eventItem);
                }

            }
            log.info("end google transaction, size = {}", eventList.size());

            int roomStartBlock = TimeAdapter.localTime2TimeBlockInt(room.getStartTime());
            int roomEndBlock = TimeAdapter.localTime2TimeBlockInt(room.getEndTime()) + 1;
            return new UserCalendarLoadPerDay(eventList, roomStartBlock, roomEndBlock);

        } catch (GeneralSecurityException e) {
            log.error("F: getAllUserCalendar / GeneralSecurityException / userId = {}, errorMsg = {}", user.getId(), e.getMessage());
            throw new CalendarLoadException(e.getMessage());
        } catch (IOException e) {
            log.error("F: getAllUserCalendar / IOException / userId = {}, errorMsg = {}", user.getId(), e.getMessage());
            throw new CalendarLoadException(e.getMessage());
        } catch (Exception e) {
            log.error("F: getAllUserCalendar / calendar load error / userId = {}, errorMsg = {}", user.getId(), e.getMessage());
            throw new CalendarLoadException(e.getMessage());
        }

    }

    public void deleteUserCalendar(Long userId, Long userCalendarId) {
        Optional<UserCalendar> userCalendarOptional = userCalendarRepository.findById(userCalendarId);

        if (userCalendarOptional.isEmpty()) {
            log.warn("E: deleteUserCalendar / userCalendar does not exist in db / userCalendarId = {}", userCalendarId);
            throw new IllegalArgumentException(ErrorMessage.userCalendarNotFound(userCalendarId));
        }

        UserCalendar userCalendar = userCalendarOptional.get();

        if(userCalendar.getUser().getId() != userId) {
            log.warn("E: deleteUserCalendar / user does not own userCalendar / userId = {}, userCalendarId = {}", userId, userCalendarId);
            throw new IllegalArgumentException(ErrorMessage.userDoesNotOwnUserCalendar(userId, userCalendarId));
        }
        if (userCalendar.isMainCalendar()) {
            log.warn("E: deleteUserCalendar / userCalendar is main calendar / userCalendarId = {}", userCalendarId);
            throw new IllegalStateException(ErrorMessage.cannotDeleteMainCalendar());
        }

        userCalendarRepository.deleteById(userCalendarId);
    }

    public void changeMainUserCalendar(String userEmail, Long newMainCalendarId) {
        User user = userService.getUserByEmail(userEmail);

        boolean success = user.changeMainCalendarTo(newMainCalendarId);

        if (success == false) {
            log.warn("E: changeMainUserCalendar / cannot find currentMainCal or newMainCal / userId = {}, newMainCalendarId = {}", user.getId(), newMainCalendarId);
            throw new IllegalArgumentException(ErrorMessage.cannotChangeMainCalendarError());
        }

    }

    public void changeSelectedSubCalendar(String userEmail, UserCalendarReq userCalendarReq) throws NoSuchElementException{
        User user = userService.getUserByEmail(userEmail);
        Long userCalendarId = userCalendarReq.getCalendarId();
        Optional<UserCalendar> userCalendarOptional = userCalendarRepository.findByUserAndIdJoinFetchSubCalendar(user, userCalendarId);

        if (userCalendarOptional.isEmpty()) {
            log.warn("E: deleteUserCalendar / userCalendar does not exist in db / userCalendarId = {}", userCalendarId);
            throw new NoSuchElementException(ErrorMessage.userCalendarNotFound(userCalendarId));
        }

        UserCalendar userCalendar = userCalendarOptional.get();
        List<SubCalendar> subCalendarList = userCalendar.getSubCalendarList();
        Map<Long, Boolean> selectedMap = userCalendarReq.getSubCalendarList().stream().collect(Collectors.toMap(
                SubCalendarReq::getSubCalendarId, SubCalendarReq::isSelected
        ));

        for (SubCalendar subCalendar : subCalendarList) {
            Long subCalendarId = subCalendar.getId();
            if(selectedMap.containsKey(subCalendarId)) {
                subCalendar.setSelected(selectedMap.get(subCalendar.getId()));
                continue;
            }
            log.warn("E: changeSelectedSubCalendar / Request contains subCalendar that is not in database / subCalendarId = {}", subCalendarId);
        }
    }
}
