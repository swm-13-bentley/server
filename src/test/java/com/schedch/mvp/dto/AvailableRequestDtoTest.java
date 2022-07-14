package com.schedch.mvp.dto;

import com.google.gson.*;
import com.schedch.mvp.adapter.LocalDateAdapter;
import com.schedch.mvp.adapter.LocalTimeAdapter;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class AvailableRequestDtoTest {

    @Test
    public void Json으로부터_Dto_생성_테스트() throws Exception {
        //given
        String participantName = "test name";
        String password = "test pwd";

        TimeBlockDto timeBlockDto1 = new TimeBlockDto(
                LocalDate.of(2022, 3, 1),
                Arrays.asList(1, 2, 4, 10, 11, 12));

        TimeBlockDto timeBlockDto2 = new TimeBlockDto(
                LocalDate.of(2022, 3, 1),
                Arrays.asList(1, 2, 4, 10, 11, 12));

        AvailableRequestDto availableRequestDto = new AvailableRequestDto();
        availableRequestDto.setParticipantName(participantName);
        availableRequestDto.setPassword(password);
        availableRequestDto.setTimeBlockDtoList(Arrays.asList(timeBlockDto1, timeBlockDto2));

        String jsonString = availableRequestDto.toString();

        //when
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                .registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {

                    @Override
                    public LocalDate deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                        return LocalDate.parse(json.getAsJsonPrimitive().getAsString());
                    }
                })
                .create();
        AvailableRequestDto mappedDto = gson.fromJson(jsonString, AvailableRequestDto.class);

        //then
        assertThat(mappedDto.getParticipantName()).isNotNull();
        assertThat(mappedDto.getPassword()).isNotNull();


    }

}