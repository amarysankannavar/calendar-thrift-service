package com.example.CalendarThriftService.repository;

import com.example.CalendarThriftService.model.EmployeeModel;
import com.example.CalendarThriftService.model.MeetingModel;
import com.example.CalendarThriftService.model.MeetingStatusModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingStatusRepo extends JpaRepository<MeetingStatusModel, Integer> {

    // You can add custom query methods if needed
    // Example: Find by employee and meeting
    List<MeetingStatusModel> findByEmployee(EmployeeModel employee);

    List<MeetingStatusModel> findByMeeting(MeetingModel meeting);

    // Example: Find by meeting and status
    List<MeetingStatusModel> findByMeetingAndStatus(MeetingModel meeting, String status);
}
