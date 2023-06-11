//package com.schedch.mvp.service.email;
//
//import com.schedch.mvp.dto.email.EmailReq;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import javax.transaction.Transactional;
//import java.time.LocalDateTime;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@Transactional
//class AwsMailServiceTest {
//
//    @Autowired
//    AwsMailService awsMailService;
//
//    @Test
//    void sendEmailBySes() {
//        awsMailService.sendEmailBySes(EmailReq.builder()
//                        .mailTo("hshlego@gmail.com")
//                        .emailTitle("something")
//                        .startDateTime(LocalDateTime.now())
//                        .endDateTime(LocalDateTime.now())
//                        .attendeeName("adsf")
//                        .dateOnly(false)
//                        .location("somewhere")
//                        .uid("someuid")
//                        .roomLink("adsfa")
//                        .roomTitle("asdfad")
//                        .sequence("1")
//                        .summary("some summary")
//                .build());
//    }
//}