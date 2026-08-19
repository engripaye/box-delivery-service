package com.ipaye.box_delivery_service.controller;

import com.ipaye.box_delivery_service.dto.*;
import com.ipaye.box_delivery_service.service.BoxService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/available")
    public ResponseEntity<List<BoxResponse>> getAvailableBoxes(){
        return ResponseEntity.ok(boxService.getAvailableBoxes());
    }

    @GetMapping("/{txref}/battery")
    public ResponseEntity<?> getBatteryCapacity(
            @PathVariable String txref
    ){
        return ResponseEntity.ok(boxService.getBatteryCapacity(txref));
    }

    @PostMapping("/{txref}/items")
    public ResponseEntity<List<ItemResponse>> loadItems(
            @PathVariable String txref,
            @Valid @RequestBody List<ItemRequest> items
    ){
        return ResponseEntity.ok(boxService.loadItems(txref, items));
    }

}
