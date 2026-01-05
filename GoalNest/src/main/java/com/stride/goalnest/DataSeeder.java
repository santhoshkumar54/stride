package com.stride.goalnest;

import com.stride.goalnest.model.Employee;
import com.stride.goalnest.repository.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(EmployeeRepository repository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if database is empty
            if (repository.count() == 0) {
                // Create Manager
                repository.save(new Employee(
                        "Alice Manager",
                        "MANAGER",
                        "manager@stride.com",
                        passwordEncoder.encode("password")
                ));

                // Create Scrum Master
                repository.save(new Employee(
                        "Bob ScrumMaster",
                        "SCRUM_MASTER",
                        "sm@stride.com",
                        passwordEncoder.encode("password")
                ));

                // Create Engineer
                repository.save(new Employee(
                        "Charlie Engineer",
                        "ENGINEER",
                        "dev@stride.com",
                        passwordEncoder.encode("password")
                ));

                System.out.println("Default users created: manager@stride.com, sm@stride.com, dev@stride.com (password: password)");
            }
        };
    }
}
