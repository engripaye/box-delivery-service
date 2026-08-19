package com.ipaye.box_delivery_service.service;

import com.ipaye.box_delivery_service.dto.*;
import com.ipaye.box_delivery_service.entity.Box;
import com.ipaye.box_delivery_service.entity.Item;
import com.ipaye.box_delivery_service.enums.BoxState;
import com.ipaye.box_delivery_service.exception.BoxAlreadyExistsException;
import com.ipaye.box_delivery_service.exception.InsufficientCapacityException;
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
        return boxRepository
                .findByStateAndBatteryCapacityGreaterThanEqual(
                        BoxState.IDLE,
                        MINIMUM_BATTERY_PERCENTAGE
                )
                .stream()
                .map(this::toBoxResponse)
                .toList();
    }

    @Override
    public List<ItemResponse>
    loadItems(
            String txref,
            List<ItemRequest> items) {

        Box box = findBox(txref);

        validateBoxCanAcceptItems(box);

        int totalItemWeight = items.stream()
                .mapToInt(ItemRequest::weight)
                .sum();

        int currentWeight = box.getItems()
                .stream()
                .mapToInt(Item::getWeight)
                .sum();

        int totalWeight = currentWeight + totalItemWeight;

        if (totalWeight > box.getWeightLimit()) {
            throw new InsufficientCapacityException(
                    "Total item weight of " + totalWeight
                            + "g exceeds the box weight limit of "
                            + box.getWeightLimit() + "g"
            );
        }

        List<Item> newItems = items.stream()
                .map(request -> {
                    Item item = new Item();

                    item.setName(request.name());
                    item.setWeight(request.weight());
                    item.setCode(request.code());
                    item.setBox(box);

                    return item;
                })
                .toList();

        List<Item> savedItems = itemRepository.saveAll(newItems);

        box.setState(BoxState.LOADED);
        boxRepository.save(box);

        return savedItems.stream()
                .map(this::toItemResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponse> getLoadedItems(String txref) {
        findBox(txref);

        return itemRepository.findByBoxTxref(txref)
                .stream()
                .map(this::toItemResponse)
                .toList();
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
