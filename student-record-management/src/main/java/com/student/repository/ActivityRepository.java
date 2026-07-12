package com.student.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.student.entity.Activity;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByStudentId(Long studentId);

}