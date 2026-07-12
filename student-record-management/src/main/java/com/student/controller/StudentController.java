package com.student.controller;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.student.entity.Student;
import com.student.repository.ActivityRepository;
import com.student.repository.StudentRepository;
@Controller
public class StudentController {


private final StudentRepository repo;
private final ActivityRepository activityRepo;
public StudentController(StudentRepository repo,
                         ActivityRepository activityRepo) {
    this.repo = repo;
    this.activityRepo = activityRepo;
}

// LOGIN PAGE
@GetMapping("/")
public String loginPage() {
    return "login";
}

// SIGNUP PAGE
@GetMapping("/signup")
public String signupPage() {
    return "signup";
}

// VIEW + SEARCH STUDENTS
@GetMapping("/students")
public String viewStudents(
        @RequestParam(value = "keyword", required = false) String keyword,
        Model model) {

    List<Student> students = repo.findAll();

    if (keyword != null && !keyword.isEmpty()) {
        students = students.stream()
                .filter(s ->
                        s.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                        s.getRegNo().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    model.addAttribute("students", students);
    model.addAttribute("keyword", keyword);

    return "students";
}

// SHOW ADD FORM
@GetMapping("/addStudent")
public String addForm(Model model) {

    model.addAttribute("student", new Student());

    return "add-student";
}

// SAVE STUDENT
@PostMapping("/saveStudent")
public String saveStudent(
        @ModelAttribute Student student,
        @RequestParam("photoFile") MultipartFile photoFile)
        throws IOException {

    Path uploadPath = Paths.get("src/main/resources/static/images").toAbsolutePath().normalize();
    Path targetClassesPath = Paths.get("target/classes/static/images").toAbsolutePath().normalize();

    // Clean up if it exists as a file (redundancy check for safety)
    if (Files.exists(uploadPath) && !Files.isDirectory(uploadPath)) {
        Files.delete(uploadPath);
    }
    if (Files.notExists(uploadPath)) {
        Files.createDirectories(uploadPath);
    }

    if (Files.exists(targetClassesPath.getParent()) && Files.notExists(targetClassesPath)) {
        Files.createDirectories(targetClassesPath);
    }

    if (!photoFile.isEmpty()) {
        // If editing and a photo already exists, delete the old one first to prevent disk accumulation
        if (student.getId() != null) {
            Student oldStudent = repo.findById(student.getId()).orElse(null);
            if (oldStudent != null && oldStudent.getPhoto() != null && !oldStudent.getPhoto().isEmpty()) {
                Path oldSrcPhoto = uploadPath.resolve(oldStudent.getPhoto());
                try {
                    Files.deleteIfExists(oldSrcPhoto);
                } catch (IOException e) {
                    System.err.println("Failed to delete old source photo: " + e.getMessage());
                }
                
                Path oldTargetPhoto = targetClassesPath.resolve(oldStudent.getPhoto());
                try {
                    Files.deleteIfExists(oldTargetPhoto);
                } catch (IOException e) {
                    System.err.println("Failed to delete old target photo: " + e.getMessage());
                }
            }
        }

        String fileName = System.currentTimeMillis() + "_" + photoFile.getOriginalFilename();

        // Save to source directory
        Files.copy(photoFile.getInputStream(),
                   uploadPath.resolve(fileName),
                   java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        // Also save to target/classes directory if it exists, for hot deployment
        if (Files.exists(targetClassesPath)) {
            Files.copy(photoFile.getInputStream(),
                       targetClassesPath.resolve(fileName),
                       java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }

        student.setPhoto(fileName);
    } else {
        // Keep old photo while editing
        if (student.getId() != null) {
            Student oldStudent = repo.findById(student.getId()).orElse(null);
            if (oldStudent != null) {
                student.setPhoto(oldStudent.getPhoto());
            }
        }
    }

    repo.save(student);
    return "redirect:/students";
}

// EDIT STUDENT
@GetMapping("/editStudent/{id}")
public String editStudent(@PathVariable Long id,
                Model model) {

    model.addAttribute("student",
            repo.findById(id)
                    .orElseThrow(() ->
                            new RuntimeException("Student not found")));

    return "add-student";
}

// STUDENT PROFILE
@GetMapping("/student/{id}")
public String studentProfile(@PathVariable Long id, Model model) {

    Student student = repo.findById(id)
            .orElseThrow(() ->
                    new RuntimeException("Student not found"));

    model.addAttribute("student", student);

    return "student-profile";
}

// DELETE STUDENT
@GetMapping("/deleteStudent/{id}")
public String deleteStudent(@PathVariable Long id) {

    repo.deleteById(id);

    return "redirect:/students";
}

}
