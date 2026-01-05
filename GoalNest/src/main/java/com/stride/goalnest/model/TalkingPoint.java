package com.stride.goalnest.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class TalkingPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String topic;
    private LocalDate createdDate;
    private Boolean isDiscussed;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    public TalkingPoint() {
    }

    public TalkingPoint(String topic, LocalDate createdDate, Boolean isDiscussed, Employee employee) {
        this.topic = topic;
        this.createdDate = createdDate;
        this.isDiscussed = isDiscussed;
        this.employee = employee;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public Boolean getIsDiscussed() {
        return isDiscussed;
    }

    public void setIsDiscussed(Boolean isDiscussed) {
        this.isDiscussed = isDiscussed;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
