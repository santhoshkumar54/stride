package com.stride.goalnest.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private LocalDate deadline;
    private String status; // IN_PROGRESS, COMPLETED

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    public Goal() {
    }

    public Goal(String description, LocalDate deadline, String status, Employee employee) {
        this.description = description;
        this.deadline = deadline;
        this.status = status;
        this.employee = employee;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
