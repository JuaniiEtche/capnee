package com.gidas.capneebe.global.controllers;


import com.gidas.capneebe.global.config.swagger.responses.SwaggerResponseCode;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@SecurityRequirement(name = "Bearer Authentication")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Test")
public class TestController extends SwaggerResponseCode {

    @GetMapping("/code-status")
    public ResponseEntity<Void> testCodeStatus(@RequestParam int code) {
        return ResponseEntity.status(code).build();
    }

}
