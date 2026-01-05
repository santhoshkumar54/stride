package com.stride.goalnest.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class FollowUp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private String topic;
    private String status; // PENDING, DONE

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    public FollowUp() {
    }

    public FollowUp(LocalDate date, String topic, String status, Employee employee) {
        this.date = date;
        this.topic = topic;
        this.status = status;
        this.employee = employee;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
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
