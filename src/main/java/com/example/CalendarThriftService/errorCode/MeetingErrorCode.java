package com.example.CalendarThriftService.errorCode;

public enum MeetingErrorCode {

       INVALID_PARTICIPANTS(1001, "At least 6 employees should be in a meeting."),
        SHORT_MEETING_DURATION(1002, "Meeting duration should be at least 30 minutes."),
        PAST_MEETING(1003, "Meeting can't be scheduled in the past."),
        OUTSIDE_WORK_HOURS(1004, "Meetings must be scheduled between 10 AM to 6 PM."),
        ROOM_NOT_FOUND(1005, "Room not found."),
        GIVEN_ROOM_NOT_AVAILABLE(1006, "The given room is not free for given time."),
        ROOMS_NOT_AVAILABLE(1006, "The rooms is not free for given time."),
        EMPLOYEES_NOT_AVAILABLE(1007, "Some employees are unavailable.");

        private final int code;
        private final String message;

        MeetingErrorCode(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }


}
