package com.thien.smart_planner_project.service;

import com.thien.smart_planner_project.model.Attendee;

import java.util.List;

public interface AttendeeService {
    List<Attendee> getListAttendeeInEvent(String eventId);
}
