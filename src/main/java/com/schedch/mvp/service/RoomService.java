package com.schedch.mvp.service;

import com.schedch.mvp.dto.RoomRequestDto;
import com.schedch.mvp.model.Room;
import com.schedch.mvp.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    public String createRoom(RoomRequestDto roomRequestDto) {
        Room room = new Room(roomRequestDto);
        Room save = roomRepository.save(room);
        return save.getUuid();
    }
}
