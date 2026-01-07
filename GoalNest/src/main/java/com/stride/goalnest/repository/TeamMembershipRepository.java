package com.stride.goalnest.repository;

import com.stride.goalnest.model.Employee;
import com.stride.goalnest.model.TeamMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TeamMembershipRepository extends JpaRepository<TeamMembership, Long> {
    List<TeamMembership> findByEmployee(Employee employee);
    List<TeamMembership> findByEmployeeAndEndDateIsNull(Employee employee);
    List<TeamMembership> findByTeam(com.stride.goalnest.model.Team team);
}
