package com.example.demo.models;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class JobStatus {

    @Id
    private String jobId;
    private String status;

    public JobStatus() {
    }

    public JobStatus(String jobId, String status) {
        this.jobId = jobId;
        this.status = status;
    }
}
