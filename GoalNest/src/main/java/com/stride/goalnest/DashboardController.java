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

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String dashboard(Model model) {
        return "dashboard";
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
        if (employee.getId() == null && (employee.getPassword() == null || employee.getPassword().isEmpty())) {
            employee.setPassword(passwordEncoder.encode("password"));
        }
        service.saveEmployee(employee);
        return "redirect:/employees";
    }

    @GetMapping("/employees/{id}")
    public String viewEmployeeDetails(@org.springframework.web.bind.annotation.PathVariable Long id, Model model) {
        Employee employee = service.getEmployeeById(id);
        if (employee == null) {
            return "redirect:/employees";
        }
        model.addAttribute("employee", employee);
        model.addAttribute("history", service.getTeamHistory(employee));
        return "employee_details";
    }

    // Teams
    @GetMapping("/teams")
    public String viewTeams(Model model) {
        model.addAttribute("teams", service.getAllTeams());
        return "teams";
    }

    @GetMapping("/teams/new")
    public String newTeamForm(Model model) {
        model.addAttribute("team", new Team());
        return "team_form";
    }

    @PostMapping("/teams")
    public String saveTeam(@ModelAttribute Team team) {
        service.saveTeam(team);
        return "redirect:/teams";
    }

    @GetMapping("/teams/{id}")
    public String viewTeamDetails(@org.springframework.web.bind.annotation.PathVariable Long id, Model model) {
        Team team = service.getTeamById(id);
        if (team == null) {
            return "redirect:/teams";
        }
        // Ideally we would want to show current members here.
        // For now let's just show the add member form in the details or a separate view.
        // But the requirement says "add individuals to team".
        // Let's pass the team and list of employees to add.
        model.addAttribute("team", team);
        model.addAttribute("employees", service.getAllEmployees());
        model.addAttribute("memberships", service.getTeamMembers(team));
        return "team_details";
    }

    @PostMapping("/teams/{id}/members")
    public String addMemberToTeam(@org.springframework.web.bind.annotation.PathVariable Long id,
                                  @org.springframework.web.bind.annotation.RequestParam Long employeeId,
                                  @org.springframework.web.bind.annotation.RequestParam String roleInTeam,
                                  @org.springframework.web.bind.annotation.RequestParam String startDate) {
        Team team = service.getTeamById(id);
        Employee employee = service.getEmployeeById(employeeId);
        if (team != null && employee != null) {
            service.addEmployeeToTeam(employee, team, roleInTeam, java.time.LocalDate.parse(startDate));
        }
        return "redirect:/teams/" + id;
    }
}
