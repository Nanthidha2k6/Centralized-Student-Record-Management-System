package com.student.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.student.repository.StudentRepository;

@Controller
public class HomeController {

    @Autowired
    private StudentRepository repo;

    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @PostMapping("/register")
public String register() {

    return "redirect:/login";
}

    @PostMapping("/dashboard")
    public String dashboard(@RequestParam String username,
                            @RequestParam String password,
                            Model model) {

        System.out.println("LOGIN TRIGGERED");

        if ("admin".equals(username) && "admin".equals(password)) {

            loadDashboardData(model);

            return "dashboard";
        }

        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboardPage(Model model) {

        loadDashboardData(model);

        return "dashboard";
    }

    private void loadDashboardData(Model model) {

        model.addAttribute("totalStudents", repo.count());

        model.addAttribute("cseCount",
                repo.countByDepartment("CSE"));

        model.addAttribute("eceCount",
                repo.countByDepartment("ECE"));

        model.addAttribute("eeeCount",
                repo.countByDepartment("EEE"));

        model.addAttribute("itCount",
                repo.countByDepartment("IT"));

        model.addAttribute("mechCount",
                repo.countByDepartment("MECH"));

        model.addAttribute("civilCount",
                repo.countByDepartment("CIVIL"));
    }
}