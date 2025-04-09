package com.example.CalendarThriftService;

import com.example.CalendarThriftService.errorCode.MeetingErrorCode;
import com.example.CalendarThriftService.generated.*;
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

import java.time.Duration;
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

    public static boolean checkMeetingPolicy(MeetingRequest meetingRequest) throws MeetingException {

        if (meetingRequest.getEmployeeIds().size() < 6) {
            throw new MeetingException(MeetingErrorCode.INVALID_PARTICIPANTS.getMessage(),
                    MeetingErrorCode.INVALID_PARTICIPANTS.getCode());
        }


        logger.info("employees are more than 6.");
           LocalTime start = LocalTime.parse(meetingRequest.getStartTime());
           LocalTime end = LocalTime.parse(meetingRequest.getEndTime());

           if (Duration.between(start, end).toMinutes() < 30)  throw new MeetingException(MeetingErrorCode.SHORT_MEETING_DURATION.getMessage(),
                   MeetingErrorCode.SHORT_MEETING_DURATION.getCode());

            if(LocalDate.parse(meetingRequest.getDate()).isBefore(LocalDate.now())){
                throw new MeetingException(MeetingErrorCode.PAST_MEETING.getMessage(),
                        MeetingErrorCode.PAST_MEETING.getCode());
            }


        LocalTime workStart = LocalTime.of(10, 0);
           LocalTime workEnd = LocalTime.of(18, 0);


           if (start.isBefore(workStart) || end.isAfter(workEnd)) {
               throw new MeetingException(MeetingErrorCode.OUTSIDE_WORK_HOURS.getMessage(),
                       MeetingErrorCode.OUTSIDE_WORK_HOURS.getCode());
           };
           logger.info("time is in between 10am to 6pm.");
        return true;
    }



    @Override
    public int canScheduleMeeting(MeetingRequest meetingRequest, int roomId) throws TException {
        logger.info("checking the policy");
        try{
            if(!checkMeetingPolicy(meetingRequest)){
                throw new MeetingException("Meeting policies are not met.",400);
            }
        } catch (MeetingException e) {
            logger.error("error while checkking the policy");
            throw e;
        }

        logger.info("entered the canScheduleMeeting method in MeetingHandlerService ");

        LocalTime start = LocalTime.parse(meetingRequest.getStartTime());
        LocalTime end = LocalTime.parse(meetingRequest.getEndTime());
        LocalDate meetingDate = LocalDate.parse(meetingRequest.getDate());
        List<Integer> employeeIds = meetingRequest.getEmployeeIds();
        List<Integer> rooms = new ArrayList<>();

        List<Integer> availableEmployees = employeeRepo.findAvailableEmpoloyees(employeeIds, meetingDate, start, end);
        logger.info("available employees are: "+availableEmployees+"rooms:"+rooms);
        if(availableEmployees.size()!=employeeIds.size()){
            throw new MeetingException(MeetingErrorCode.EMPLOYEES_NOT_AVAILABLE.getMessage(),
                    MeetingErrorCode.EMPLOYEES_NOT_AVAILABLE.getCode());
        }
        if(roomId>0){
            logger.info("Checking room with ID: " + roomId);
            Optional<MeetingRoomModel> room = meetingRoomRepo.findById(roomId);

            if (!room.isPresent() || !room.get().isAvailable()) {
                logger.error("Room with ID " + roomId + " does not exist.");
                throw new MeetingException(MeetingErrorCode.ROOM_NOT_FOUND.getMessage(), MeetingErrorCode.ROOM_NOT_FOUND.getCode());
            }


            int cnt = meetingRoomRepo.checkRoomAvailabilityInTheDuration(roomId,meetingDate,start,end);
            logger.info("Availability check result for Room " + roomId + ": " + cnt);

            if(cnt==0){
                logger.info("the given room is not available.");
                throw new MeetingException(MeetingErrorCode.GIVEN_ROOM_NOT_AVAILABLE.getMessage(), MeetingErrorCode.GIVEN_ROOM_NOT_AVAILABLE.getCode());
            }
            else {
                logger.info("returning value:"+roomId);
                return roomId;
            }
        }
        else{
            rooms = meetingRoomRepo.findAvailableRoomsOnDateAndTime(meetingDate,start,end);
            logger.info("rooms are fetched "+rooms);
            if(rooms.isEmpty()){
                throw new MeetingException(MeetingErrorCode.ROOMS_NOT_AVAILABLE.getMessage(), MeetingErrorCode.ROOMS_NOT_AVAILABLE.getCode());
            }
            logger.info("available rooms are: "+rooms);
        }







        logger.info("returning value:"+roomId);
        return rooms.get(0);

    }



    @Override
    public MeetingResponse scheduleMeeting(MeetingInformation meetingInformation, MeetingRequest meetingRequest, int roomId) throws TException {
        // Create a new meeting and assign an ID

        int availableRoomId = canScheduleMeeting(meetingRequest,roomId);
        if(availableRoomId<=0){
            return new MeetingResponse(-1,-1,null);
        }
        logger.info("schedule meeting after can schedule");

        String description = meetingInformation.getDescription();
        String agenda = meetingInformation.getAgenda();
        LocalDate formatDate = LocalDate.parse(meetingRequest.getDate());
        LocalTime start = LocalTime.parse(meetingRequest.getStartTime());
        LocalTime end = LocalTime.parse(meetingRequest.getEndTime());
        List<Integer> employeeIds = meetingRequest.getEmployeeIds();



        MeetingRoomModel roomModel = meetingRoomRepo.findById(availableRoomId).orElse(null);
        MeetingModel model = new MeetingModel(description,agenda,roomModel,formatDate,start,end,true);
        logger.info("before saving the model");

      MeetingModel saved =  meetingRepo.save(model);

        List<MeetingStatusModel> statusList = new ArrayList<>();
        logger.info("bedore employeeid for loop");
        for (Integer employeeId : employeeIds) {
            EmployeeModel emp = employeeRepo.findEmployeeById(employeeId);
            MeetingStatusModel status = new MeetingStatusModel(emp,saved,"accepted");

            status.setStatus("Scheduled");
            statusList.add(status);
        }
        logger.info("after employeeid for loop");

        // Save all meeting statuses
        meetingStatusRepo.saveAll(statusList);

        String roomName =meetingRoomRepo.findRoomNameById(availableRoomId);
        logger.info("the room name is:"+roomName);



        logger.info("just before return statement");

        // Return the assigned meeting ID
        return new MeetingResponse(saved.getId(),availableRoomId,roomName);
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
