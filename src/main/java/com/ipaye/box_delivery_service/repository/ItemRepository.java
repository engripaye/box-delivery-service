package com.ipaye.box_delivery_service.repository;

import com.ipaye.box_delivery_service.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByBoxTxref(String txref);
}
