package com.ipaye.box_delivery_service.repository;

import com.ipaye.box_delivery_service.entity.Box;
import com.ipaye.box_delivery_service.enums.BoxState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoxRepository extends JpaRepository<Box, Long> {

    Optional<Box> findByTxref(String txref);

    Boolean existsByTxref(String txref);

    List<Box> findByStateAndBatterCapacityGreaterThanEqual(
            BoxState state,
            Integer batteryCapacity
    );
}
