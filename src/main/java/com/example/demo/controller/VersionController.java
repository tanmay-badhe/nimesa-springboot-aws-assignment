package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class VersionController {
    @GetMapping("/version")
    public ResponseEntity<?> version(){
        return ResponseEntity.ok("hi");
    }
}
