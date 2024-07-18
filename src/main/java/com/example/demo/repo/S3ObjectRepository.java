package com.example.demo.repo;

import com.example.demo.models.S3Object;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface S3ObjectRepository extends JpaRepository<S3Object, Long> {
    @Query("SELECT COUNT(s) FROM S3Object s WHERE s.bucketName = :bucketName")
    long countByBucketName(@Param("bucketName") String bucketName);
    @Query("SELECT s FROM S3Object s WHERE s.bucketName = :bucketName AND s.objectKey LIKE CONCAT('%', :pattern, '%')")
    List<S3Object> findByBucketNameAndPattern(@Param("bucketName") String bucketName, @Param("pattern") String pattern);

}