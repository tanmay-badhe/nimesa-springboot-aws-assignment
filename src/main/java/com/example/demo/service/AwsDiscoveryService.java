package com.example.demo.service;
import com.example.demo.models.EC2Instance;
import com.example.demo.models.JobStatus;
import com.example.demo.models.S3Bucket;
import com.example.demo.models.S3Object;
import com.example.demo.repo.EC2InstanceRepository;
import com.example.demo.repo.JobStatusRepository;
import com.example.demo.repo.S3BucketRepository;
import com.example.demo.repo.S3ObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Reservation;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
//import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class AwsDiscoveryService {

    @Autowired
    private Ec2Client ec2Client;

    @Autowired
    private S3Client s3Client;
    @Autowired
    private EC2InstanceRepository ec2InstanceRepository;

    @Autowired
    private S3BucketRepository s3BucketRepository;
    @Autowired
    private JobStatusRepository jobStatusRepository;
    @Autowired
    private S3ObjectRepository s3ObjectRepository;
//    public void discoverServices(List<String> services, String jobId) {
//        if (services.contains("EC2")) {
//            CompletableFuture.runAsync(this::discoverEC2Instances);
//        }
//        if (services.contains("S3")) {
//            CompletableFuture.runAsync(this::discoverS3Buckets);
//        }
//    }
public void discoverServices(List<String> services, String jobId) {
    JobStatus jobStatus = new JobStatus(jobId, "In Progress");
    jobStatusRepository.save(jobStatus);

    CompletableFuture<Void> ec2Future = CompletableFuture.runAsync(() -> {
        try {
            if (services.contains("EC2")) {
                discoverEC2Instances();
            }
        } catch (Exception e) {
            jobStatus.setStatus("Failed");
            jobStatusRepository.save(jobStatus);
        }
    });

    CompletableFuture<Void> s3Future = CompletableFuture.runAsync(() -> {
        try {
            if (services.contains("S3")) {
                discoverS3Buckets();
            }
        } catch (Exception e) {
            jobStatus.setStatus("Failed");
            jobStatusRepository.save(jobStatus);
        }
    });

    CompletableFuture.allOf(ec2Future, s3Future).thenRun(() -> {
        jobStatus.setStatus("Success");
        jobStatusRepository.save(jobStatus);
    });
}

    private void discoverEC2Instances() {
        DescribeInstancesRequest request = DescribeInstancesRequest.builder().build();
        DescribeInstancesResponse response = ec2Client.describeInstances(request);
        // Persist EC2 instance information in the database
        for (Reservation reservation : response.reservations()) {
            for (Instance instance : reservation.instances()) {
                EC2Instance ec2Instance = new EC2Instance();
                ec2Instance.setInstanceId(instance.instanceId());
                // Set other fields
                ec2InstanceRepository.save(ec2Instance);
            }
        }
    }

    private void discoverS3Buckets() {
        ListBucketsRequest request = ListBucketsRequest.builder().build();
        ListBucketsResponse response = s3Client.listBuckets(request);
        // Persist S3 bucket information in the database
        for (Bucket bucket : response.buckets()) {
            S3Bucket s3Bucket = new S3Bucket();
            s3Bucket.setBucketName(bucket.name());
            // Set other fields
            s3BucketRepository.save(s3Bucket);
        }
    }

    private S3Client createS3Client(String bucketRegion) {
        return S3Client.builder()
                .region(Region.of(bucketRegion))
                .build();
    }
    public void discoverS3Objects(String bucketName, String jobId) {
        JobStatus jobStatus = new JobStatus(jobId, "In Progress");
        jobStatusRepository.save(jobStatus);

//        GetBucketLocationRequest locationRequest = GetBucketLocationRequest.builder()
//                .bucket(bucketName)
//                .build();
//
//        String bucketRegion = s3Client.getBucketLocation(locationRequest).locationConstraintAsString();
//        S3Client s3ClientForBucket = createS3Client(bucketRegion);

        CompletableFuture.runAsync(() -> {
            try {
                ListObjectsRequest request = ListObjectsRequest.builder()
                        .bucket(bucketName)
                        .build();
                ListObjectsResponse response = s3Client.listObjects(request);
//                System.out.println("zz"+response.contents());
//                List<software.amazon.awssdk.services.s3.model.S3Object> objects = response.contents();
                for (software.amazon.awssdk.services.s3.model.S3Object s3ObjectSummary : response.contents()) {
                    S3Object s3Object = new S3Object(bucketName, s3ObjectSummary.key());
                   s3ObjectRepository.save(s3Object);
                }


                jobStatus.setStatus("Success");
            } catch (Exception e) {
                System.out.println("zz"+e.getMessage()+"\n zz"+e);
                jobStatus.setStatus("Failed");

            } finally {
                jobStatusRepository.save(jobStatus);
            }
        });
    }
    public long getS3ObjectCount(String bucketName) {
        return s3ObjectRepository.countByBucketName(bucketName);
    }
    public List<String> getS3ObjectsLike(String bucketName, String pattern) {
        List<S3Object> s3Objects = s3ObjectRepository.findByBucketNameAndPattern(bucketName, pattern);
        return s3Objects.stream().map(S3Object::getObjectKey).collect(Collectors.toList());
    }

}
