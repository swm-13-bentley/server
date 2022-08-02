package com.schedch.mvp.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.schedch.mvp.config.NotionConfig;
import com.schedch.mvp.dto.feedback.FeedbackRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class FeedbackServiceNotionImpl implements FeedbackService{

    private final NotionConfig notionConfig;
    private final Gson gson;

    @Override
    public boolean saveFeedback(FeedbackRequest feedbackRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", notionConfig.getAuthCode());
        headers.add("Notion-Version", notionConfig.getVersion());
        headers.setContentType(MediaType.APPLICATION_JSON);

        JsonObject body = new JsonObject();

        JsonObject parent = new JsonObject();
        parent.addProperty("database_id", notionConfig.getDbId());

        JsonObject properties = new JsonObject();

        //set type column
        JsonObject type = new JsonObject();
        JsonArray title = new JsonArray();
        JsonObject text = new JsonObject();
        JsonObject title_content = new JsonObject();
        title_content.addProperty("content", feedbackRequest.getType());
        text.add("text", title_content);
        title.add(text);
        type.add("title", title);
        properties.add("Type", type);

        //set content column
        JsonObject content = new JsonObject();
        JsonArray richText = new JsonArray();
        JsonObject richTextObj = new JsonObject();
        JsonObject richTxtContent = new JsonObject();
        richTxtContent.addProperty("content", feedbackRequest.getContent());
        richTextObj.addProperty("type", "text");
        richTextObj.add("text", richTxtContent);
        richText.add(richTextObj);
        content.add("rich_text", richText);
        properties.add("Content", content);

        //set email column
        JsonObject email = new JsonObject();
        email.addProperty("email",
                (feedbackRequest.getEmail() == "" || feedbackRequest.getEmail() == null) ? "no@email.com" : feedbackRequest.getEmail());
        properties.add("Email", email);

        //set datetime column
        JsonObject datetime = new JsonObject();
        JsonObject date = new JsonObject();
        date.addProperty("start",
                ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime().toString());
        datetime.add("date", date);
        properties.add("DateTime", datetime);

        //set body
        body.add("parent", parent);
        body.add("properties", properties);

        HttpEntity<String> request = new HttpEntity<>(gson.toJson(body), headers);

        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                notionConfig.getUrl(), //{요청할 서버 주소}
                HttpMethod.POST, //{요청할 방식}
                request, // {요청할 때 보낼 데이터}
                String.class //{요청시 반환되는 데이터 타입}
        );

        return response.getStatusCode().is2xxSuccessful();
    }
}
