package com.student.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.student.entity.Activity;
import com.student.repository.ActivityRepository;

@Controller
public class ActivityController {

    @Autowired
    private ActivityRepository activityRepo;

    @GetMapping("/student/{studentId}/activities")
    public String viewActivities(@PathVariable Long studentId,
                                 Model model) {

        List<Activity> activities =
                activityRepo.findByStudentId(studentId);

        model.addAttribute("activities", activities);
        model.addAttribute("studentId", studentId);

        return "activities";
    }

    @GetMapping("/student/{studentId}/addActivity")
    public String addActivityForm(@PathVariable Long studentId, Model model) {

        Activity activity = new Activity();
        activity.setStudentId(studentId);

        model.addAttribute("activity", activity);

        return "add-activity";
    }
    @GetMapping("/deleteActivity/{id}")
public String deleteActivity(@PathVariable Long id) {

    Activity activity =
            activityRepo.findById(id).orElse(null);
             if (activity == null) {
        return "redirect:/students";
    }


    Long studentId = activity.getStudentId();

    activityRepo.deleteById(id);

    return "redirect:/student/" +
            studentId +
            "/activities";
}

    @PostMapping("/saveActivity")
    public String saveActivity(@ModelAttribute Activity activity) {

        activityRepo.save(activity);

        return "redirect:/student/" +
                activity.getStudentId() +
                "/activities";
    }
}