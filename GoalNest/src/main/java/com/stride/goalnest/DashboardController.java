
package com.example.goalnest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("leaves", 2);
        model.addAttribute("tasks", 5);
        model.addAttribute("goals", 3);
        return "dashboard";
    }
}
