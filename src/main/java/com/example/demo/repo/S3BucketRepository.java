package com.example.demo.repo;

import com.example.demo.models.S3Bucket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface S3BucketRepository extends JpaRepository<S3Bucket, Long> {
}