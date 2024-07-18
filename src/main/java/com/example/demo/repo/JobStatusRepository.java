package com.example.demo.repo;

import com.example.demo.models.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobStatusRepository extends JpaRepository<JobStatus, String> {
}
