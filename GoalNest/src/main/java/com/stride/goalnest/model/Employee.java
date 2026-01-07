package com.stride.goalnest.model;

import javax.persistence.*;

@Entity
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String role; // MANAGER, SCRUM_MASTER, ENGINEER
    private String email;
    private String password;
    @Column(name = "job_level")
    private String jobLevel; // T1A, T1B, T2A, T2B, T3A, T3B, T4, T5, SM, M

    @Column(name = "emp_type")
    private String employeeType; // Front end developer, Back end developer, Full stack developer, Management, QA

    @Column(name = "github_username")
    private String githubUsername;

    public Employee() {
    }

    public Employee(String name, String role, String email, String password) {
        this.name = name;
        this.role = role;
        this.email = email;
        this.password = password;
    }

    public Employee(String name, String role, String email, String password, String jobLevel, String employeeType, String githubUsername) {
        this.name = name;
        this.role = role;
        this.email = email;
        this.password = password;
        this.jobLevel = jobLevel;
        this.employeeType = employeeType;
        this.githubUsername = githubUsername;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getJobLevel() {
        return jobLevel;
    }

    public void setJobLevel(String jobLevel) {
        this.jobLevel = jobLevel;
    }

    public String getEmployeeType() {
        return employeeType;
    }

    public void setEmployeeType(String employeeType) {
        this.employeeType = employeeType;
    }

    public String getGithubUsername() {
        return githubUsername;
    }

    public void setGithubUsername(String githubUsername) {
        this.githubUsername = githubUsername;
    }
}
