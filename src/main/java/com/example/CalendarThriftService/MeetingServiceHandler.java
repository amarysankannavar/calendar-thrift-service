package com.example.CalendarThriftService;

import com.example.CalendarThriftService.generated.EmployeeMeetingStatus;
import com.example.CalendarThriftService.generated.Meeting;
import com.example.CalendarThriftService.generated.MeetingManage;
import com.example.CalendarThriftService.model.EmployeeModel;
import com.example.CalendarThriftService.model.MeetingModel;
import com.example.CalendarThriftService.model.MeetingRoomModel;
import com.example.CalendarThriftService.model.MeetingStatusModel;
import com.example.CalendarThriftService.repository.EmployeeRepo;
import com.example.CalendarThriftService.repository.MeetingRepo;
import com.example.CalendarThriftService.repository.MeetingRoomRepo;
import com.example.CalendarThriftService.repository.MeetingStatusRepo;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class MeetingServiceHandler implements MeetingManage.Iface {
    @Autowired
    MeetingRoomRepo meetingRoomRepo;

    @Autowired
    MeetingRepo meetingRepo;

    @Autowired
    MeetingStatusRepo meetingStatusRepo;

    @Autowired
    EmployeeRepo employeeRepo;

    private static final Logger logger = LoggerFactory.getLogger(MeetingServiceHandler.class);


    private List<Meeting> meetings = new ArrayList<>();
    private List<EmployeeMeetingStatus> statuses = new ArrayList<>();

    @Override
    public boolean canScheduleMeeting(List<Integer> employeeIds, String date, String startTime, String endTime) throws TException {

        // Simulate checking if any employee has a meeting at the given time
        // For simplicity, assume no conflicts in the demo
        logger.info("entered the canScheduleMeeting method in MeetingHandlerService ");

        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);
        LocalDate meetingDate = LocalDate.parse(date);
        List<Integer> rooms = meetingRoomRepo.findAvailableRoomsOnDateAndTime(meetingDate,start,end);
        logger.info("rooms are fetched "+rooms);
        List<Integer> availableEmployees = employeeRepo.findAvailableEmpoloyees(employeeIds, meetingDate, start, end);
        logger.info("available employees are: "+availableEmployees);
        if(availableEmployees.size()!=employeeIds.size()){
            return false;
        }
        if(rooms.isEmpty()){
            return false;
        }
        logger.info("available rooms are: "+rooms);


        return true;

    }

    @Override
    public int scheduleMeeting(String description, String agenda, List<Integer> employeeIds, String date, String startTime, String endTime, int roomId) throws TException {
        // Create a new meeting and assign an ID

        if(!canScheduleMeeting(employeeIds,date,startTime,endTime)){
            return 0;
        }


        LocalDate formatDate = LocalDate.parse(date);
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);
        List<Integer> rooms = meetingRoomRepo.findAvailableRoomsOnDateAndTime(formatDate,start,end);
        int scheduledRoom = rooms.get(0);

        MeetingRoomModel roomModel = meetingRoomRepo.findById(scheduledRoom).orElse(null);
        MeetingModel model = new MeetingModel(description,agenda,roomModel,formatDate,start,end,true);


      MeetingModel saved =  meetingRepo.save(model);

        List<MeetingStatusModel> statusList = new ArrayList<>();
        for (Integer employeeId : employeeIds) {
            EmployeeModel emp = employeeRepo.findEmployeeById(employeeId);
            MeetingStatusModel status = new MeetingStatusModel(emp,saved,"accepted");

            status.setStatus("Scheduled");
            statusList.add(status);
        }

        // Save all meeting statuses
        meetingStatusRepo.saveAll(statusList);





        // Return the assigned meeting ID
        return saved.getId();
    }

    @Override
    public void cancelMeeting(int meetingId) throws TException {
        // Find and remove the meeting with the given ID
        meetings.removeIf(meeting -> meeting.getMeetingId() == meetingId);

        // Optionally, remove the meeting statuses as well
        statuses.removeIf(status -> status.getMeetingId() == meetingId);
    }

    @Override
    public void updateMeetingStatus(EmployeeMeetingStatus statusUpdate) throws TException {
        // Find and update the meeting status
        for (EmployeeMeetingStatus status : statuses) {
            if (status.getMeetingId() == statusUpdate.getMeetingId() &&
                    status.getEmployeeId() == statusUpdate.getEmployeeId()) {
                status.setStatus(statusUpdate.getStatus());
                return;
            }
        }
        // If no existing status, add the new status to the list
        statuses.add(statusUpdate);
    }

    @Override
    public Meeting getMeetingDetails(int meetingId) throws TException {
        // Find and return the meeting details for the given meeting ID
        for (Meeting meeting : meetings) {
            if (meeting.getMeetingId() == meetingId) {
                return meeting;
            }
        }
        // Return null if no meeting found
        return null;
    }

    @Override
    public List<Meeting> getMeetings(int employeeId, String filterType) throws TException {
        // Return all meetings for the given employee (for simplicity, no actual filtering implemented)
        List<Meeting> employeeMeetings = new ArrayList<>();
        for (Meeting meeting : meetings) {
            if (meeting.getEmployeeIds().contains(employeeId)) {
                employeeMeetings.add(meeting);
            }
        }
        return employeeMeetings;
    }

    @Override
    public List<EmployeeMeetingStatus> getMeetingStatuses(int meetingId) throws TException {
        // Return the statuses of all employees for a given meeting
        List<EmployeeMeetingStatus> meetingStatuses = new ArrayList<>();
        for (EmployeeMeetingStatus status : statuses) {
            if (status.getMeetingId() == meetingId) {
                meetingStatuses.add(status);
            }
        }
        return meetingStatuses;
    }
}
