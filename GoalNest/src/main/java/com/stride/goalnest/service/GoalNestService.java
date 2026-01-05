package com.stride.goalnest.service;

import com.stride.goalnest.model.*;
import com.stride.goalnest.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoalNestService {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private LeaveRequestRepository leaveRequestRepository;
    @Autowired
    private GoalRepository goalRepository;
    @Autowired
    private FollowUpRepository followUpRepository;
    @Autowired
    private PerformancePointRepository performancePointRepository;
    @Autowired
    private TalkingPointRepository talkingPointRepository;
    @Autowired
    private RoleRatingRepository roleRatingRepository;

    // Employee Methods
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id).orElse(null);
    }

    // Leave Request Methods
    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestRepository.findAll();
    }

    public LeaveRequest saveLeaveRequest(LeaveRequest leaveRequest) {
        return leaveRequestRepository.save(leaveRequest);
    }

    // Goal Methods
    public List<Goal> getAllGoals() {
        return goalRepository.findAll();
    }

    public Goal saveGoal(Goal goal) {
        return goalRepository.save(goal);
    }

    // FollowUp Methods
    public List<FollowUp> getAllFollowUps() {
        return followUpRepository.findAll();
    }

    public FollowUp saveFollowUp(FollowUp followUp) {
        return followUpRepository.save(followUp);
    }

    // PerformancePoint Methods
    public List<PerformancePoint> getAllPerformancePoints() {
        return performancePointRepository.findAll();
    }

    public PerformancePoint savePerformancePoint(PerformancePoint performancePoint) {
        return performancePointRepository.save(performancePoint);
    }

    // TalkingPoint Methods
    public List<TalkingPoint> getAllTalkingPoints() {
        return talkingPointRepository.findAll();
    }

    public TalkingPoint saveTalkingPoint(TalkingPoint talkingPoint) {
        return talkingPointRepository.save(talkingPoint);
    }

    // RoleRating Methods
    public List<RoleRating> getAllRoleRatings() {
        return roleRatingRepository.findAll();
    }

    public RoleRating saveRoleRating(RoleRating roleRating) {
        return roleRatingRepository.save(roleRating);
    }
}
