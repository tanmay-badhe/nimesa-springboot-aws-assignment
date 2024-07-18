package com.example.demo.controller;

import com.example.demo.service.AwsDiscoveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/bucket")
public class BucketController {
    @Autowired
    private AwsDiscoveryService awsDiscoveryService;

    @PostMapping("/discoverS3Objects")
    public String discoverS3Objects(@RequestBody String bucketName) {
        String jobId = UUID.randomUUID().toString();
        CompletableFuture.runAsync(() -> awsDiscoveryService.discoverS3Objects(bucketName, jobId));
        return jobId;
    }
    @GetMapping("/getS3ObjectCount")
    public long getS3ObjectCount(@RequestParam String bucketName) {
        return awsDiscoveryService.getS3ObjectCount(bucketName);
    }
    @GetMapping("/getS3ObjectsLike")
    public List<String> getS3ObjectsLike(@RequestParam String bucketName, @RequestParam String pattern) {
        return awsDiscoveryService.getS3ObjectsLike(bucketName, pattern);
    }
}
