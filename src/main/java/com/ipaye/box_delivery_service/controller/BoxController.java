package com.ipaye.box_delivery_service.controller;

import com.ipaye.box_delivery_service.dto.BoxResponse;
import com.ipaye.box_delivery_service.dto.CreateBoxRequest;
import com.ipaye.box_delivery_service.service.BoxService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/boxes")
@RequiredArgsConstructor
public class BoxController {

    private final BoxService boxService;

    @PostMapping
    public ResponseEntity<BoxResponse> createBox(
            @Valid @RequestBody CreateBoxRequest request){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(boxService.createBox(request));
    }


}
