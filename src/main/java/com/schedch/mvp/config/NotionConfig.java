package com.schedch.mvp.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Getter
@PropertySource("classpath:notion.yaml")
public class NotionConfig {

    @Value("${mvp.auth}")
    String authCode;

    @Value("${mvp.feedback.db.id}")
    String dbId;

    @Value("${notion.version}")
    String version;

    @Value("${notion.api.url}")
    String url;
}
