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

    @Query("SELECT r.roomId FROM MeetingRoomModel r WHERE NOT EXISTS " +
            "(SELECT 1 FROM MeetingModel m WHERE m.meetingRoom.roomId = r.roomId " +
            "AND m.date = :date " +
            "AND (m.startTime < :endTime AND m.endTime > :startTime) " +
            "AND m.isActive = true)")
    List<Integer> findAvailableRoomsOnDateAndTime(LocalDate date, LocalTime startTime, LocalTime endTime);

    @Query("SELECT r.roomName FROM MeetingRoomModel r WHERE r.roomId=:roomId")
    String findRoomNameById(int roomId);

    Optional<MeetingRoomModel> findById(Integer roomId);

    @Query("SELECT COUNT(r) FROM MeetingRoomModel r WHERE r.roomId = :roomId AND NOT EXISTS " +
            "(SELECT 1 FROM MeetingModel m WHERE m.meetingRoom.roomId = r.roomId " +
            "AND m.date = :date " +
            "AND (m.startTime < :endTime AND m.endTime > :startTime) " +
            "AND m.isActive = true)")
    int checkRoomAvailabilityInTheDuration(int roomId, LocalDate date, LocalTime startTime, LocalTime endTime);

}
