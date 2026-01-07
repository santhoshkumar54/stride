package com.stride.goalnest;

import com.stride.goalnest.model.Employee;
import com.stride.goalnest.model.RepoConfig;
import com.stride.goalnest.repository.EmployeeRepository;
import com.stride.goalnest.repository.RepoConfigRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(EmployeeRepository repository, RepoConfigRepository repoConfigRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if database is empty
            if (repository.count() == 0) {
                // Create Manager
                repository.save(new Employee(
                        "Alice Manager",
                        "MANAGER",
                        "manager@stride.com",
                        passwordEncoder.encode("password"),
                        "M",
                        "Management",
                        "alice_gh"
                ));

                // Create Scrum Master
                repository.save(new Employee(
                        "Bob ScrumMaster",
                        "SCRUM_MASTER",
                        "sm@stride.com",
                        passwordEncoder.encode("password"),
                        "SM",
                        "Management",
                        "bob_gh"
                ));

                // Create Engineer
                repository.save(new Employee(
                        "Charlie Engineer",
                        "ENGINEER",
                        "dev@stride.com",
                        passwordEncoder.encode("password"),
                        "T2A",
                        "Full stack developer",
                        "charlie_gh"
                ));

                System.out.println("Default users created: manager@stride.com, sm@stride.com, dev@stride.com (password: password)");
            }

            if (repoConfigRepository.count() == 0) {
                repoConfigRepository.save(new RepoConfig("blackboard-learn", "learn", OffsetDateTime.now().minusDays(30), true));
                repoConfigRepository.save(new RepoConfig("blackboard-learn", "ultra", OffsetDateTime.now().minusDays(30), true));
                System.out.println("Default repos seeded: blackboard-learn/learn, blackboard-learn/ultra");
            }
        };
    }
}
