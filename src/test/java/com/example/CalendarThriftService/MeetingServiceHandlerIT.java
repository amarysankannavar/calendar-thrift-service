package com.example.CalendarThriftService;

import com.example.CalendarThriftService.generated.MeetingException;
import com.example.CalendarThriftService.model.MeetingModel;
import com.example.CalendarThriftService.model.MeetingRoomModel;
import com.example.CalendarThriftService.model.OfficeModel;
import com.example.CalendarThriftService.repository.EmployeeRepo;
import com.example.CalendarThriftService.repository.MeetingRepo;
import com.example.CalendarThriftService.repository.MeetingRoomRepo;
import com.example.CalendarThriftService.repository.MeetingStatusRepo;
import org.apache.thrift.TException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class MeetingServiceHandlerIT {

    @Autowired
    private MeetingServiceHandler meetingServiceHandler;

    @MockBean
    private MeetingRoomRepo meetingRoomRepo;

    @MockBean
    private MeetingRepo meetingRepo;

    @MockBean
    private MeetingStatusRepo meetingStatusRepo;

    @MockBean
    private EmployeeRepo employeeRepo;
/*
    @Test
    void testCanScheduleMeeting_Success() throws TException {
        // Arrange
        List<Integer> employeeIds = Arrays.asList(1, 2, 3, 4, 5, 6);
        String date = "2025-02-21";
        String startTime = "10:30";
        String endTime = "11:00";

        LocalDate meetingDate = LocalDate.parse(date);
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);

        when(meetingRoomRepo.findAvailableRoomsOnDateAndTime(meetingDate, start, end))
                .thenReturn(Arrays.asList(101));
        when(employeeRepo.findAvailableEmpoloyees(employeeIds, meetingDate, start, end))
                .thenReturn(employeeIds);


        // Act
        boolean result = meetingServiceHandler.canScheduleMeeting(employeeIds, date, startTime, endTime,2);

        // Assert
        assertTrue(result, "Meeting should be schedulable");
    }

    @Test
    void testCanScheduleMeeting_Failure_DueToFewEmployees() {
        // Arrange
        List<Integer> employeeIds = Arrays.asList(1, 2, 3, 4, 5);
        String date = "2025-02-21";
        String startTime = "10:30";
        String endTime = "11:00";


        // Act & Assert
        MeetingException exception = assertThrows(MeetingException.class, () ->
                meetingServiceHandler.canScheduleMeeting(employeeIds, date, startTime, endTime,2));

        assertEquals("Atleast 6 employees are required.", exception.getMessage());
    }

    @Test
    void testScheduleMeeting_Success() throws TException {
        // Arrange
        List<Integer> employeeIds = Arrays.asList(1, 2, 3, 4, 5, 6);
        String date = "2025-02-21";
        String startTime = "10:30";
        String endTime = "11:00";
        String description = "Project Discussion";
        String agenda = "Discuss project roadmap";
        int roomId = 101;

        LocalDate meetingDate = LocalDate.parse(date);
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);

        MeetingRoomModel roomModel = new MeetingRoomModel("s3", "Conference Room A",new OfficeModel("google","bengaluru"));
        MeetingModel savedMeeting = new MeetingModel(description, agenda, roomModel, meetingDate, start, end, true);
        savedMeeting.setId(123);  // Simulated saved meeting ID

        when(meetingRoomRepo.findAvailableRoomsOnDateAndTime(meetingDate, start, end))
                .thenReturn(Collections.singletonList(roomId));
        when(employeeRepo.findAvailableEmpoloyees(employeeIds, meetingDate, start, end))
                .thenReturn(employeeIds);
        when(meetingRoomRepo.findById(roomId)).thenReturn(Optional.of(roomModel));
        when(meetingRepo.save(Mockito.<MeetingModel>any())).thenReturn(savedMeeting);
        // Act
        int meetingId = meetingServiceHandler.scheduleMeeting(description, agenda, employeeIds, date, startTime, endTime, roomId);

        // Assert
        assertEquals(123, meetingId, "Meeting ID should match the saved entity");
    } */
}
