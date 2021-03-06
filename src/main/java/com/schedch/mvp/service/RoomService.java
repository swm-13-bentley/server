package com.schedch.mvp.service;

import com.schedch.mvp.dto.RoomRequestDto;
import com.schedch.mvp.dto.RoomResponseDto;
import com.schedch.mvp.model.Room;
import com.schedch.mvp.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;
    public String createRoom(RoomRequestDto roomRequestDto) {
        Room room = new Room(roomRequestDto);
        Room save = roomRepository.save(room);
        return save.getUuid();
    }

    public Room getRoom(String roomUuid) {
        Optional<Room> roomOptional = roomRepository.findByUuid(roomUuid);
        Room room = roomOptional.orElseThrow(
                () -> new NoSuchElementException(String.format("Room for uuid: %s not found", roomUuid))
        );
        return room;
    }

    public RoomResponseDto getRoomDto(String roomUuid) {
        Room room = getRoom(roomUuid);
        return new RoomResponseDto(room);
    }

}
