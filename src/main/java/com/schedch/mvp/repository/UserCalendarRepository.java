package com.schedch.mvp.repository;

import com.schedch.mvp.model.User;
import com.schedch.mvp.model.UserCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserCalendarRepository extends JpaRepository<UserCalendar, Long> {

    Optional<UserCalendar> findByCalendarEmailAndUser(String calendarEmail, User user);

    @Query("select distinct c from UserCalendar c" +
            " join fetch c.subCalendarList" +
            " where c.user = :user" +
            " and c.id = :userCalendarId")
    Optional<UserCalendar> findByUserAndIdJoinFetchSubCalendar(@Param("user") User user,
                                                           @Param("userCalendarId") Long userCalendarId);

    @Query("select distinct c from UserCalendar c" +
            " join fetch c.subCalendarList" +
            " where c.user = :user")
    List<UserCalendar> findAllByUserJoinFetchSubCalendar(@Param("user") User user);

    @Query("select distinct c from UserCalendar c" +
            " join fetch c.subCalendarList" +
            " where c.mainCalendar = true")
    Optional<UserCalendar> findMainCalendarByUserJoinFetchSubCalendar(User user);
}
