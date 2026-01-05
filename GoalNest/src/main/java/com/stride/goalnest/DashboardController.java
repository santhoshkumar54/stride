package com.stride.goalnest;

import com.stride.goalnest.model.*;
import com.stride.goalnest.service.GoalNestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class DashboardController {

    @Autowired
    private GoalNestService service;

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("leaves", service.getAllLeaveRequests().size());
        model.addAttribute("goals", service.getAllGoals().size());
        model.addAttribute("followups", service.getAllFollowUps().size());
        return "dashboard";
    }

    // Leave Requests
    @GetMapping("/leaves")
    public String viewLeaves(Model model) {
        model.addAttribute("leaveRequests", service.getAllLeaveRequests());
        return "leaves";
    }

    @GetMapping("/leaves/new")
    public String newLeaveForm(Model model) {
        model.addAttribute("leaveRequest", new LeaveRequest());
        model.addAttribute("employees", service.getAllEmployees());
        return "leave_form";
    }

    @PostMapping("/leaves")
    public String saveLeave(@ModelAttribute LeaveRequest leaveRequest) {
        service.saveLeaveRequest(leaveRequest);
        return "redirect:/leaves";
    }

    // Goals
    @GetMapping("/goals")
    public String viewGoals(Model model) {
        model.addAttribute("goals", service.getAllGoals());
        return "goals";
    }

    @GetMapping("/goals/new")
    public String newGoalForm(Model model) {
        model.addAttribute("goal", new Goal());
        model.addAttribute("employees", service.getAllEmployees());
        return "goal_form";
    }

    @PostMapping("/goals")
    public String saveGoal(@ModelAttribute Goal goal) {
        service.saveGoal(goal);
        return "redirect:/goals";
    }

    // FollowUps
    @GetMapping("/followups")
    public String viewFollowUps(Model model) {
        model.addAttribute("followUps", service.getAllFollowUps());
        return "followups";
    }

    @GetMapping("/followups/new")
    public String newFollowUpForm(Model model) {
        model.addAttribute("followUp", new FollowUp());
        model.addAttribute("employees", service.getAllEmployees());
        return "followup_form";
    }

    @PostMapping("/followups")
    public String saveFollowUp(@ModelAttribute FollowUp followUp) {
        service.saveFollowUp(followUp);
        return "redirect:/followups";
    }

    // Performance Points
    @GetMapping("/points")
    public String viewPoints(Model model) {
        model.addAttribute("points", service.getAllPerformancePoints());
        return "points";
    }

    @GetMapping("/points/new")
    public String newPointForm(Model model) {
        model.addAttribute("performancePoint", new PerformancePoint());
        model.addAttribute("employees", service.getAllEmployees());
        return "point_form";
    }

    @PostMapping("/points")
    public String savePoint(@ModelAttribute PerformancePoint performancePoint) {
        service.savePerformancePoint(performancePoint);
        return "redirect:/points";
    }

    // Talking Points
    @GetMapping("/talkingpoints")
    public String viewTalkingPoints(Model model) {
        model.addAttribute("talkingPoints", service.getAllTalkingPoints());
        return "talkingpoints";
    }

    @GetMapping("/talkingpoints/new")
    public String newTalkingPointForm(Model model) {
        model.addAttribute("talkingPoint", new TalkingPoint());
        model.addAttribute("employees", service.getAllEmployees());
        return "talkingpoint_form";
    }

    @PostMapping("/talkingpoints")
    public String saveTalkingPoint(@ModelAttribute TalkingPoint talkingPoint) {
        service.saveTalkingPoint(talkingPoint);
        return "redirect:/talkingpoints";
    }

    // Role Ratings
    @GetMapping("/ratings")
    public String viewRatings(Model model) {
        model.addAttribute("ratings", service.getAllRoleRatings());
        return "ratings";
    }

    @GetMapping("/ratings/new")
    public String newRatingForm(Model model) {
        model.addAttribute("roleRating", new RoleRating());
        model.addAttribute("employees", service.getAllEmployees());
        return "rating_form";
    }

    @PostMapping("/ratings")
    public String saveRating(@ModelAttribute RoleRating roleRating) {
        service.saveRoleRating(roleRating);
        return "redirect:/ratings";
    }

    // Employees
    @GetMapping("/employees")
    public String viewEmployees(Model model) {
        model.addAttribute("employees", service.getAllEmployees());
        return "employees";
    }

    @GetMapping("/employees/new")
    public String newEmployeeForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "employee_form";
    }

    @PostMapping("/employees")
    public String saveEmployee(@ModelAttribute Employee employee) {
        service.saveEmployee(employee);
        return "redirect:/employees";
    }
}
