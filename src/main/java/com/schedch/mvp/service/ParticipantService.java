package com.schedch.mvp.service;

import com.schedch.mvp.dto.ParticipantResponseDto;
import com.schedch.mvp.model.Participant;
import com.schedch.mvp.model.Room;
import com.schedch.mvp.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ParticipantService {

    private final RoomRepository roomRepository;

    public ParticipantResponseDto findUnSignedParticipantAndValidate(
            String roomUuid, String participantName, String password) throws Exception {

        //방에 참여하는 참여자 정보 다 가져옴
        Optional<Room> roomOptional = roomRepository.findByUuid(roomUuid);
        Room room = roomOptional.orElseThrow(
                () -> new NoSuchElementException(String.format("Room for uuid: %s not found", roomUuid)));
        //이 부분은 RoomService 불러서 해야할지, 근데 그러면 exception throw 가 명시적으로 보이지 않아서 걱정.

        List<Participant> foundParticipant = room.getParticipantList().stream()
                .filter(p -> p.isSignedIn() == false && p.getParticipantName().equals(participantName))
                .collect(Collectors.toList());

        if(foundParticipant.isEmpty()) {
            //신규 유저 -> 유저 등록해야 함
            Participant newParticipant = new Participant(participantName, password, false);
            room.addParticipant(newParticipant);
            return new ParticipantResponseDto(newParticipant);
        }

        Participant participant = foundParticipant.get(0);

        if(participant.checkPassword(password)) {
            //기존 유저가 맞음 -> 기존 시간 돌려주면 됨
            return new ParticipantResponseDto(participant);
        }
        else {
            //기존 유저이나, 비밀번호가 틀렸음
            throw new IllegalAccessException("password is incorrect for participant: " + participantName);
       }
    }
}
