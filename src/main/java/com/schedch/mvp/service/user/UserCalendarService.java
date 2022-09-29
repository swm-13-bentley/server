package com.schedch.mvp.service.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.schedch.mvp.config.ErrorMessage;
import com.schedch.mvp.dto.oauth.GoogleLoginDto;
import com.schedch.mvp.dto.user.SubCalendarReq;
import com.schedch.mvp.dto.user.UserCalendarLoadRes;
import com.schedch.mvp.dto.user.UserCalendarReq;
import com.schedch.mvp.exception.CalendarLoadException;
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
            log.info("User added already existing calendar for email : {}", calendarEmail);
            return;
        }

        addCalendarToUser(googleLoginDto, user);
    }

    public UserCalendar addCalendarToUser(GoogleLoginDto googleLoginDto, User user) throws CalendarLoadException {
        try {
            //토큰으로 캘린더 불러오기
            TokenResponse tokenResponse = googleLoginDto.toTokenResponse();
            Calendar gCal = calendarService.getGoogleCalendarByTokenResponse(tokenResponse);

            //캘린더로 calendarList 불러오기
            CalendarList gCalendarList = gCal.calendarList().list().execute();

            //UserCalendar 객체 생성 후 추가
            UserCalendar userCalendar = googleLoginDto.toUserCalendar();
            user.addUserCalendar(userCalendar);

            //SubCalendar 객체 생성 후 추가
            List<CalendarListEntry> items = gCalendarList.getItems();
            items.stream().forEach(calendarListEntry -> {
                String subCalendarName = calendarListEntry.getSummary();
                String gCalId = calendarListEntry.getId();
                Boolean selected = calendarListEntry.getSelected();
                SubCalendar subCalendar = new SubCalendar(subCalendarName, selected, gCalId);
                userCalendar.addSubCalendar(subCalendar);
            });

            log.info("calendar added for userId = {}, subCalendarSize = {}", user.getId(), items.size());
            return userCalendar;

        } catch (GeneralSecurityException e) {
            throw new CalendarLoadException(e.getMessage());
        } catch (IOException e) {
            throw new CalendarLoadException(e.getMessage());
        }
    }

    public List<UserCalendar> getAllUserCalendar(String userEmail) {
        User user = userService.getUserByEmail(userEmail);
        List<UserCalendar> userCalendarList = userCalendarRepository.findAllByUserJoinFetchSubCalendar(user);
        log.info("getAllUserCalendar for userId = {}, size = {}", user.getId(), userCalendarList.size());

        return userCalendarList;
    }

    public List<UserCalendarLoadRes> loadCalendarEvents(String userEmail, DateTime startDateTime, DateTime endDateTime) {
        User user = userService.getUserByEmail(userEmail);
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

        //create return entity
        List<UserCalendarLoadRes> userCalendarLoadResList = new ArrayList<>();

        try {
            Calendar gCal = calendarService.getGoogleCalendarByTokenResponse(tokenResponse);
            List<CalendarListEntry> calendarsToShow = gCal.calendarList().list().execute().getItems().stream()
                    .filter(item -> selectedSubCalSet.contains(item.getId()))
                    .collect(Collectors.toList());

            for (CalendarListEntry calendarListEntry : calendarsToShow) {
                Events events = gCal.events().list(calendarListEntry.getId())
                        .setTimeMin(startDateTime)
                        .setTimeMax(endDateTime)
                        .setOrderBy("startTime")
                        .setSingleEvents(true)
                        .execute();

                List<Event> eventsItems = events.getItems();

                for (Event eventItem : eventsItems) {
                    if (eventItem.getStart().getDateTime() == null) { //하루 종일로 설정 된 이벤트
                        continue;
                    }

                    userCalendarLoadResList.add(new UserCalendarLoadRes(eventItem));
                }

            }

            return userCalendarLoadResList;

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            throw new CalendarLoadException(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            throw new CalendarLoadException(e.getMessage());
        }

    }

    public void deleteUserCalendar(Long userCalendarId) {
        Optional<UserCalendar> userCalendarOptional = userCalendarRepository.findById(userCalendarId);

        if (userCalendarOptional.isEmpty()) {
            throw new IllegalArgumentException(ErrorMessage.userCalendarNotFound(userCalendarId));
        }

        UserCalendar userCalendar = userCalendarOptional.get();
        if (userCalendar.isMainCalendar()) {
            throw new IllegalStateException(ErrorMessage.cannotDeleteMainCalendar());
        }

        userCalendarRepository.deleteById(userCalendarId);
    }

    public void changeMainUserCalendar(String userEmail, Long newMainCalendarId) {
        User user = userService.getUserByEmail(userEmail);

        boolean success = user.changeMainCalendarTo(newMainCalendarId);

        if (success == false) {
            log.warn("cannot find currentMainCal or newMainCal for userId = {}, newMainCalendarId = {}", user.getId(), newMainCalendarId);
            throw new IllegalArgumentException(ErrorMessage.cannotChangeMainCalendarError());
        }

    }

    public void changeSelectedSubCalendar(String userEmail, UserCalendarReq userCalendarReq) throws NoSuchElementException{
        User user = userService.getUserByEmail(userEmail);
        Long userCalendarId = userCalendarReq.getId();
        Optional<UserCalendar> userCalendarOptional = userCalendarRepository.findByUserAndIdJoinFetchSubCalendar(user, userCalendarId);

        if (userCalendarOptional.isEmpty()) {
            throw new NoSuchElementException(ErrorMessage.userCalendarNotFound(userCalendarId));
        }

        UserCalendar userCalendar = userCalendarOptional.get();
        List<SubCalendar> subCalendarList = userCalendar.getSubCalendarList();
        Map<Long, Boolean> selectedMap = userCalendarReq.getSubCalendarList().stream().collect(Collectors.toMap(
                SubCalendarReq::getId, SubCalendarReq::isSelected
        ));

        for (SubCalendar subCalendar : subCalendarList) {
            Long subCalendarId = subCalendar.getId();
            if(selectedMap.containsKey(subCalendarId)) {
                subCalendar.setSelected(selectedMap.get(subCalendar.getId()));
                continue;
            }
            log.warn("Request contains subCalendar that is not in database for subCalendarId = {}", subCalendarId);
        }
    }
}
