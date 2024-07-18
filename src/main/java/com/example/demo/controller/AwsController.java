package com.example.demo.controller;

import com.example.demo.models.EC2Instance;
import com.example.demo.models.S3Bucket;
import com.example.demo.models.JobStatus;
import com.example.demo.repo.EC2InstanceRepository;
import com.example.demo.repo.JobStatusRepository;
import com.example.demo.repo.S3BucketRepository;
import com.example.demo.service.AwsDiscoveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/aws")
public class AwsController {
    @Autowired
    private AwsDiscoveryService awsDiscoveryService;
    @Autowired
    private EC2InstanceRepository ec2InstanceRepository;
    @Autowired
    private S3BucketRepository s3BucketRepository;

    @PostMapping("/discoverServices")
    public String discoverServices(@RequestBody List<String> services) {
        String jobId = UUID.randomUUID().toString();
        CompletableFuture.runAsync(() -> awsDiscoveryService.discoverServices(services, jobId));
        return jobId;
    }

    @GetMapping("/discoveryResult/{service}")
    public Object getDiscoveryResult(@PathVariable String service) {
        if ("EC2".equalsIgnoreCase(service)) {
            List<String> instanceIds = ec2InstanceRepository.findAll()
                    .stream()
                    .map(EC2Instance::getInstanceId)
                    .collect(Collectors.toList());
            return instanceIds;
        } else if ("S3".equalsIgnoreCase(service)) {
            List<String> bucketNames = s3BucketRepository.findAll()
                    .stream()
                    .map(S3Bucket::getBucketName)
                    .collect(Collectors.toList());
            return bucketNames;
        } else {
            return "Service not supported";
        }
    }
}