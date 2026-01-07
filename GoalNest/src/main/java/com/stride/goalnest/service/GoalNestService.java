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
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private TeamMembershipRepository teamMembershipRepository;

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

    // Team Methods
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public Team getTeamById(Long id) {
        return teamRepository.findById(id).orElse(null);
    }

    public Team saveTeam(Team team) {
        return teamRepository.save(team);
    }

    // Team Membership Methods
    public List<TeamMembership> getTeamHistory(Employee employee) {
        return teamMembershipRepository.findByEmployee(employee);
    }

    public TeamMembership getCurrentTeam(Employee employee) {
        List<TeamMembership> memberships = teamMembershipRepository.findByEmployeeAndEndDateIsNull(employee);
        if (memberships.isEmpty()) {
            return null;
        }
        return memberships.get(0);
    }

    public List<TeamMembership> getTeamMembers(Team team) {
        return teamMembershipRepository.findByTeam(team);
    }

    public void addEmployeeToTeam(Employee employee, Team team, String roleInTeam, java.time.LocalDate startDate) {
        // Close current membership if exists
        TeamMembership current = getCurrentTeam(employee);
        if (current != null) {
            current.setEndDate(startDate); // End previous team on the new start date (or day before?)
            // Assuming simplified logic: new start date is the transition date.
            teamMembershipRepository.save(current);
        }

        TeamMembership newMembership = new TeamMembership(employee, team, roleInTeam, startDate);
        teamMembershipRepository.save(newMembership);
    }
}
