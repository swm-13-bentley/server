package com.schedch.mvp.service.email;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import com.schedch.mvp.dto.email.EmailReq;
import com.schedch.mvp.dto.email.MakerAlarmReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Properties;

@Service
@RequiredArgsConstructor
@Slf4j
public class AwsMailService{

    private final AmazonSimpleEmailService amazonSimpleEmailService;
    private final TemplateEngine templateEngine;

    public void sendEmailToMaker(MakerAlarmReq makerAlarmReq) {
        try {
            SendRawEmailRequest sendRawEmailRequest = getSendRawEmailRequest(makerAlarmReq);
            amazonSimpleEmailService.sendRawEmail(sendRawEmailRequest);
        } catch (Exception e){
            log.info("F: sendEmailBySes / email send error / roomLink = {}, emailReq.mailTo = {}, errorMsg = {}",
                    makerAlarmReq.getRoomLink(), makerAlarmReq.getMailTo(), e.getMessage());

        }
        log.info("S: sendEmailBySes / emailReq.mailTo = {}", makerAlarmReq.getMailTo());
    }

    public void sendEmailBySes(EmailReq emailReq) {
        try {
            SendRawEmailRequest sendRawEmailRequest = getSendRawEmailRequest(emailReq);
            amazonSimpleEmailService.sendRawEmail(sendRawEmailRequest);
        } catch (Exception e){
            log.info("F: sendEmailBySes / email send error / roomLink = {}, emailReq.mailTo = {}, errorMsg = {}",
                    emailReq.getRoomLink(), emailReq.getMailTo(), e.getMessage());

        }
        log.info("S: sendEmailBySes / emailReq.mailTo = {}", emailReq.getMailTo());
    }

    public SendRawEmailRequest getSendRawEmailRequest(EmailReq emailReq) throws MessagingException, IOException {
        Session session = Session.getDefaultInstance(new Properties());
        MimeMessage message = new MimeMessage(session);

        // Define mail emailTitle
        String emailTitle = emailReq.getEmailTitle();
        message.setSubject(emailTitle);

        // Define mail Sender
        message.setFrom("manna.time.2022@gmail.com");

        // Define mail Receiver
        String receiver = emailReq.getMailTo();
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));

        // Create a multipart/alternative child container.
        MimeMultipart msg_body = new MimeMultipart("alternative");

        // Create a wrapper for the HTML and text parts.
        MimeBodyPart wrap = new MimeBodyPart();

        // Define the HTML part.
        MimeBodyPart htmlPart = new MimeBodyPart();
        String html = getHTML(emailReq);
        htmlPart.setContent(html, "text/html; charset=UTF-8");

        // Add the text and HTML parts to the child container.
        msg_body.addBodyPart(htmlPart);

        // Add the child container to the wrapper object.
        wrap.setContent(msg_body);

        // Create a multipart/mixed parent container.
        MimeMultipart msg = new MimeMultipart("mixed");

        // Add the parent container to the message.
        message.setContent(msg);

        // Add the multipart/alternative part to the message.
        msg.addBodyPart(wrap);

        // Define the attachment
        MimeBodyPart att = new MimeBodyPart();
        att.setDataHandler(new DataHandler(new ByteArrayDataSource(getIcsString(emailReq), "text/calendar;method=REQUEST;name=\"invite.ics\"")));

        // Add the attachment to the message.
        msg.addBodyPart(att);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        message.writeTo(outputStream);
        RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));
        return new SendRawEmailRequest(rawMessage);

    }

    public SendRawEmailRequest getSendRawEmailRequest(MakerAlarmReq makerAlarmReq) throws MessagingException, IOException {
        Session session = Session.getDefaultInstance(new Properties());
        MimeMessage message = new MimeMessage(session);

        // Define mail emailTitle
        String emailTitle = String.format("언제만나에서 인원 알림을 보내드립니다: ", makerAlarmReq.getRoomTitle());
        message.setSubject(emailTitle);

        // Define mail Sender
        message.setFrom("manna.time.2022@gmail.com");

        // Define mail Receiver
        String receiver = makerAlarmReq.getMailTo();
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));

        // Create a multipart/alternative child container.
        MimeMultipart msg_body = new MimeMultipart("alternative");

        // Create a wrapper for the HTML and text parts.
        MimeBodyPart wrap = new MimeBodyPart();

        // Define the HTML part.
        MimeBodyPart htmlPart = new MimeBodyPart();
        String html = getHTML(makerAlarmReq);
        htmlPart.setContent(html, "text/html; charset=UTF-8");

        // Add the text and HTML parts to the child container.
        msg_body.addBodyPart(htmlPart);

        // Add the child container to the wrapper object.
        wrap.setContent(msg_body);

        // Create a multipart/mixed parent container.
        MimeMultipart msg = new MimeMultipart("mixed");

        // Add the parent container to the message.
        message.setContent(msg);

        // Add the multipart/alternative part to the message.
        msg.addBodyPart(wrap);

        // Add the attachment to the message.
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        message.writeTo(outputStream);
        RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));
        return new SendRawEmailRequest(rawMessage);

    }

    public String getIcsString(EmailReq emailReq) {
        String icsString = String.format("BEGIN:VCALENDAR\n" +
                        "PRODID:-//Microsoft Corporation//Outlook 10.0 MIMEDIR//EN\n" +
                        "VERSION:2.0\n" +
                        "METHOD:REQUEST\n" +
                        "BEGIN:VTIMEZONE\n" +
                        "TZID:Asia/Seoul\n" +
                        "LAST-MODIFIED:20201011T015911Z\n" +
                        "TZURL:http://tzurl.org/zoneinfo-outlook/Asia/Seoul\n" +
                        "X-LIC-LOCATION:Asia/Seoul\n" +
                        "BEGIN:STANDARD\n" +
                        "TZNAME:KST\n" +
                        "TZOFFSETFROM:+0900\n" +
                        "TZOFFSETTO:+0900\n" +
                        "DTSTART:19700101T000000\n" +
                        "END:STANDARD\n" +
                        "END:VTIMEZONE\n" +
                        "BEGIN:VEVENT\n" +
                        "DTSTAMP:%s\n" +
                        "ATTENDEE;CN=\"%s\";ROLE=REQ-PARTICIPANT;RSVP=TRUE:MAILTO:%s\n" +
                        "ORGANIZER;CN=\"%s\":MAILTO:manna.time.2022@gmail.com\n" +
                        "DTSTART;TZID=Asia/Seoul:%s\n" +
                        "DTEND;TZID=Asia/Seoul:%s\n" +
                        "LOCATION:%s\n" +
                        "TRANSP:OPAQUE\n" +
                        "SEQUENCE:%s\n" +
                        "UID:%s\n" +
                        "SUMMARY:%s\n" +
                        "PRIORITY:5\n" +
                        "CLASS:PUBLIC\n" +
                        "END:VEVENT\n" +
                        "END:VCALENDAR\n",
                LocalDateTime.now(), //DTSTAMP: 메일 작성된 시간 (ex_20220924T000951Z)
                emailReq.getAttendeeName(), //ATTENDEE: 참석자 이름
                emailReq.getMailTo(), //MAILTO: 참석자 이메일
                emailReq.getAttendeeName(), //ORGANIZER: 원래는 주관자 이름인데, 이메일 표시를 위해 참석자 이름으로 대체
                emailReq.getStartDateTimeString(), //DTSTART: 일정 시작 시간(ex_20220924T130000)
                emailReq.getEndDateTimeString(), //DTEND: 일정 끝나는 시간(ex_20220924T160000)
                emailReq.getLocation(), //LOCATION: 장소
                emailReq.getSequence(), //SEQUENCE: revision(수정) 인듯?
                emailReq.getUid(), //UID: 구별 목적
                emailReq.getSummary() //SUMMARY: 일정 이름. 이메일 ics 파일 최상단에 표시
        );

        return icsString;

    }

    public String getHTML(EmailReq emailReq) {
        String roomTitle = emailReq.getRoomTitle();
        LocalDateTime start = emailReq.getStartDateTime();
        LocalDateTime end = emailReq.getEndDateTime();
        String link = emailReq.getRoomLink();

        Context context = new Context();
        context.setVariable("roomTitle", roomTitle);
        context.setVariable("timeString",
                emailReq.isDateOnly() ? getDayString(start) : getTimeString(start, end));
        context.setVariable("link", link);

        return templateEngine.process("email-template", context);
    }

    public String getHTML(MakerAlarmReq makerAlarmReq) {
        String roomTitle = makerAlarmReq.getRoomTitle();
        String roomLink = makerAlarmReq.getRoomLink();
        int alarmNumber = makerAlarmReq.getAlarmNumber();

        Context context = new Context();
        context.setVariable("headLine", String.format("약속에 %d명이 참여했어요", alarmNumber));
        context.setVariable("roomTitle", roomTitle);
        context.setVariable("link", roomLink);
        context.setVariable("alarmNumber", alarmNumber + "명");

        return templateEngine.process("maker-alarm", context);
    }

    private String getTimeString(LocalDateTime start, LocalDateTime end) {
        if (start.toLocalDate().isEqual(end.toLocalDate())) {
            return String.format("%d년 %d월 %d일(%s) %d시 %02d분 ~ %d시 %02d분",
                    start.getYear(),
                    start.getMonthValue(),
                    start.getDayOfMonth(),
                    start.getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.KOREAN),
                    start.getHour(),
                    start.getMinute(),
                    end.getHour(),
                    end.getMinute()
            );
        }

        return String.format("%d년 %d월 %d일(%s) %d시 %02d분 ~ %d년 %d월 %d일(%s) %d시 %02d분",
                start.getYear(),
                start.getMonthValue(),
                start.getDayOfMonth(),
                start.getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.KOREAN),
                start.getHour(),
                start.getMinute(),
                end.getYear(),
                end.getMonthValue(),
                end.getDayOfMonth(),
                end.getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.KOREAN),
                end.getHour(),
                end.getMinute()
        );
    }

    private String getDayString(LocalDateTime start) {
        return String.format("%d년 %d월 %d일(%s) - 하루 종일",
                start.getYear(),
                start.getMonthValue(),
                start.getDayOfMonth(),
                start.getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.KOREAN)
            );
    }
}