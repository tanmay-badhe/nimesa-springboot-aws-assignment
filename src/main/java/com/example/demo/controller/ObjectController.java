package com.example.demo.controller;

import com.example.demo.models.JobStatus;
import com.example.demo.repo.JobStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
@RestController
@RequestMapping("/api/object")
public class ObjectController {
    @Autowired
    private JobStatusRepository jobStatusRepository;
    @GetMapping("/jobStatus/{jobId}")
    public String getJobStatus(@PathVariable String jobId) {
        Optional<JobStatus> jobStatus = jobStatusRepository.findById(jobId);
        return jobStatus.map(JobStatus::getStatus).orElse("Job ID not found");
    }

}
