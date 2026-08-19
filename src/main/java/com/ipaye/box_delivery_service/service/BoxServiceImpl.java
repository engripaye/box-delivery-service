package com.ipaye.box_delivery_service.service;

import com.ipaye.box_delivery_service.dto.*;
import com.ipaye.box_delivery_service.entity.Box;
import com.ipaye.box_delivery_service.enums.BoxState;
import com.ipaye.box_delivery_service.exception.BoxAlreadyExistsException;
import com.ipaye.box_delivery_service.repository.BoxRepository;
import com.ipaye.box_delivery_service.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BoxServiceImpl implements BoxService {

    private static final int MINIMUM_BATTERY_PERCENTAGE = 25;

    private final BoxRepository boxRepository;
    private final ItemRepository itemRepository;

    @Override
    public BoxResponse createBox(CreateBoxRequest request) {

        if (boxRepository.existsByTxref(request.txref())) {
            throw new BoxAlreadyExistsException(request.txref());
        }

        Box box = new Box();

        box.setTxref(request.txref());
        box.setWeightLimit(request.weightLimit());
        box.setBatteryCapacity(request.batteryCapacity());
        box.setState(BoxState.IDLE);

        Box savedBox = boxRepository.save(box);

        return toBoxResponse(savedBox);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoxResponse> getAvailableBoxes() {
        return List.of();
    }

    @Override
    public List<ItemResponse> loadItems(String txref, List<ItemRequest> items) {
        return List.of();
    }

    @Override
    public List<ItemResponse> getLoadedItems(String txref) {
        return List.of();
    }

    @Override
    public BatteryResponse getBatteryCapacity(String txref) {
        return null;
    }

    @Override
    public BoxResponse createBox(CreateBoxRequest createBoxRequest) {
        return null;
    }
}
