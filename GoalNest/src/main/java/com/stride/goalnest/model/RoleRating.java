package com.stride.goalnest.model;

import javax.persistence.*;

@Entity
public class RoleRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String criteria;
    private Integer score; // 1-5 or 1-10

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    public RoleRating() {
    }

    public RoleRating(String criteria, Integer score, Employee employee) {
        this.criteria = criteria;
        this.score = score;
        this.employee = employee;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
