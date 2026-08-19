package com.ipaye.box_delivery_service.service;

import com.ipaye.box_delivery_service.dto.CreateBoxRequest;
import com.ipaye.box_delivery_service.dto.ItemRequest;
import com.ipaye.box_delivery_service.entity.Box;
import com.ipaye.box_delivery_service.entity.Item;
import com.ipaye.box_delivery_service.enums.BoxState;
import com.ipaye.box_delivery_service.exception.*;
import com.ipaye.box_delivery_service.repository.BoxRepository;
import com.ipaye.box_delivery_service.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoxServiceImplTest {

    @Mock
    private BoxRepository boxRepository;

    @Mock
    private ItemRepository itemRepository;

    private BoxServiceImpl boxService;

    @BeforeEach
    void setUp() {
        boxService = new BoxServiceImpl(
                boxRepository,
                itemRepository);
    }

    @Test
    void shouldCreateBoxSuccessfully() {
        CreateBoxRequest request = new CreateBoxRequest(
                "BOX100",
                500,
                80
        );

        when(boxRepository.existsByTxref("BOX100"))
                .thenReturn(false);

        Box savedBox = new Box();
        savedBox.setId(1L);
        savedBox.setTxref("BOX100");
        savedBox.setWeightLimit(500);
        savedBox.setBatteryCapacity(80);
        savedBox.setState(BoxState.IDLE);

        when(boxRepository.save(any(Box.class)))
                .thenReturn(savedBox);

        var response = boxService.createBox(request);

        assertNotNull(response);
        assertEquals("BOX100", response.txref());
        assertEquals(500, response.weightLimit());
        assertEquals(80, response.batteryCapacity());
        assertEquals(BoxState.IDLE, response.state());

        verify(boxRepository).save(any(Box.class));
    }

    @Test
    void shouldRejectDuplicateBox(){

        CreateBoxRequest request = new CreateBoxRequest(
                "BOX100",
                500,
                80
        );

        when(boxRepository.existsByTxref("BOX100"))
                .thenReturn(true);

        assertThrows(
                BoxAlreadyExistsException.class,
                () -> boxService.createBox(request)
        );

        verify(boxRepository, never()).save(any(Box.class));

    }

    @Test
    void shouldThrowExceptionWhenBoxDoesNotExist(){

        when(boxRepository.findByTxref("UNKNOWN"))
                .thenReturn(Optional.empty());

        assertThrows(
                BoxNotFoundException.class,
                () -> boxService.getBatteryCapacity("UNKNOWN")
        );
    }

    private Box createBox(
            String txref,
            int weightLimit,
            int battery,
            BoxState state
    ){
        Box box = new Box();

        box.setId(1L);
        box.setTxref(txref);
        box.setWeightLimit(weightLimit);
        box.setBatteryCapacity(battery);
        box.setState(state);
        box.setItems(new ArrayList<>());

        return box;
    }

    @Test
    void shouldRejectLoadingBatteryIsBelowMinimum(){
        Box box = createBox(
                "BOX100",
                500,
                20,
                BoxState.IDLE
        );

        when(boxRepository.findByTxref("BOX100"))
                .thenReturn(Optional.of(box));

        List<ItemRequest> items = List.of(
                new ItemRequest(
                        "Laptop",
                        100,
                        "ITEM_001"
                )
        );

        assertThrows(
                InsufficientBatteryException.class,
                () -> boxService.loadItems("BOX100", items)
        );

        verify(itemRepository, never()).saveAll(any());
    }

    @Test
    void shouldRejectLoadingWhenBoxIsNotIdle(){
        Box box = createBox(
                "BOX100",
                500,
                80,
                BoxState.LOADED
        );

        when(boxRepository.findByTxref("BOX100"))
                .thenReturn(Optional.of(box));

        List<ItemRequest> items = List.of(
                new ItemRequest(
                        "Laptop",
                        100,
                        "ITEM_001"
                )
        );

        assertThrows(
                InvalidBoxStateException.class,
                () -> boxService.loadItems("BOX100", items)
        );

        verify(itemRepository, never()).saveAll(any());
    }

    @Test
    void shouldRejectItemsWhenWeightExceedsCapacity(){
        Box box = createBox(
                "BOX100",
                500,
                80,
                BoxState.IDLE
        );

        Item existingItem = new Item();
        existingItem.setWeight(400);
        existingItem.setBox(box);

        box.setItems(new ArrayList<>(List.of(existingItem)));

        when(boxRepository.findByTxref("BOX100"))
                .thenReturn(Optional.of(box));

        List<ItemRequest> items = List.of(
                new ItemRequest(
                        "Laptop",
                        150,
                        "ITEM_001"
                )
        );

        assertThrows(
                InsufficientCapacityException.class,
                () -> boxService.loadItems("BOX100", items)
        );

        verify(itemRepository, never()).saveAll(any());
    }

    @Test
    void shouldLoadItemsSuccessfully() {

        Box box = createBox(
                "BOX100",
                500,
                80,
                BoxState.IDLE
        );

        when(boxRepository.findByTxref("BOX100"))
                .thenReturn(Optional.of(box));

        List<ItemRequest> requests = List.of(
                new ItemRequest(
                        "Laptop",
                        200,
                        "ITEM_001"
                ),
                new ItemRequest(
                        "Mouse",
                        50,
                        "ITEM_002"
                )
        );

        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Laptop");
        item1.setWeight(200);
        item1.setCode("ITEM_001");
        item1.setBox(box);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Mouse");
        item2.setWeight(50);
        item2.setCode("ITEM_002");
        item2.setBox(box);

        when(itemRepository.saveAll(any()))
                .thenReturn(List.of(item1, item2));

        var response = boxService.loadItems(
                "BOX100",
                requests
        );

        assertEquals(2, response.size());
        assertEquals("Laptop", response.get(0).name());
        assertEquals("Mouse", response.get(1).name());

        assertEquals(
                BoxState.LOADED,
                box.getState()
        );

        verify(itemRepository).saveAll(any());
    }

    @Test
    void shouldRejectEmptyItemList(){
        assertThrows(
                IllegalArgumentException.class,
                () -> boxService.loadItems(
                        "BOX100",
                        List.of()
                )
        );

        verifyNoInteractions(boxRepository);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void shouldReturnAvailableBoxes(){
        Box box1 = createBox(
                "BOX001",
                500,
                80,
                BoxState.IDLE
        );

        Box box2 = createBox(
                "BOX002",
                400,
                90,
                BoxState.IDLE
        );

        when(boxRepository
                .findByStateAndBatteryCapacityGreaterThanEqual(
                        BoxState.IDLE,
                        25
                ))
                .thenReturn(List.of(box1, box2));

        var response = boxService.getAvailableBoxes();

        assertEquals(2, response.size());

        assertEquals(
                "BOX001",
                response.get(0).txref()
        );

        assertEquals(
                "BOX002",
                response.get(1).txref()
        );
    }

    @Test
    void shouldReturnBatteryCapacity(){
        Box box = createBox(
                "BOX100",
                500,
                85,
                BoxState.IDLE
        );

        when(boxRepository.findByTxref("BOX100"))
                .thenReturn(Optional.of(box));

        var response = boxService.getBatteryCapacity("BOX100");

        assertEquals("BOX100", response.txref());
        assertEquals(85, response.batteryCapacity());
    }
}
