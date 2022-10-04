package com.schedch.mvp.service.calendar;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.calendar.Calendar;
import com.schedch.mvp.config.oauth.GoogleConfigUtils;
import com.schedch.mvp.service.GoogleCalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CalendarService {

    private final GoogleConfigUtils googleConfigUtils;

    public Calendar getGoogleCalendarByTokenResponse(TokenResponse tokenResponse) throws GeneralSecurityException, IOException {
        NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        String credentialsFilePath = googleConfigUtils.getCREDENTIALS_FILE_PATH();
        JsonFactory json_factory = googleConfigUtils.getJSON_FACTORY();
        List<String> scopeList = googleConfigUtils.getScopeList();

        InputStream in = GoogleCalendarService.class.getResourceAsStream(credentialsFilePath);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(json_factory, new InputStreamReader(in));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, json_factory, clientSecrets, scopeList)
                .setAccessType("offline")
                .build();

        Credential credential = flow.createAndStoreCredential(tokenResponse, "userId");

        return new Calendar.Builder(HTTP_TRANSPORT, json_factory, credential)
                .setApplicationName("Mannatime")
                .build();
    }
}
