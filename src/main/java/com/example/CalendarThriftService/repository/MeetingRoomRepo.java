package com.example.CalendarThriftService.repository;

import com.example.CalendarThriftService.model.MeetingRoomModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface MeetingRoomRepo extends JpaRepository<MeetingRoomModel, Integer> {
    @Query("SELECT m FROM MeetingRoomModel m WHERE m.available = true")
    List<MeetingRoomModel> findActiveMeetingRooms();

    @Query("SELECT r.roomId FROM MeetingRoomModel r WHERE r.roomId NOT IN (SELECT m.meetingRoom.roomId FROM MeetingModel m WHERE m.date = :date AND (m.startTime < :endTime AND m.endTime > :startTime))")
    List<Integer> findAvailableRoomsOnDateAndTime(LocalDate date, LocalTime startTime, LocalTime endTime);

    Optional<MeetingRoomModel> findById(Integer roomId);

}
