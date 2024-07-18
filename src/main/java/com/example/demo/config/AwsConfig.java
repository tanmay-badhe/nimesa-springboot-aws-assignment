package com.example.demo.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

@Configuration
public class AwsConfig {

    @Bean
    public Ec2Client ec2Client() {
        return Ec2Client.builder()
                .region(Region.AP_SOUTH_1) // Mumbai region
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("accessKeyID", "secretAccessKey")))
                .build();
    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.AP_SOUTH_1) // Mumbai region
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("accessKeyID", "secretAccessKey")))
                .build();



    }
}