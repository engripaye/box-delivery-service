package com.ipaye.box_delivery_service.service;

import com.ipaye.box_delivery_service.dto.CreateBoxRequest;
import com.ipaye.box_delivery_service.entity.Box;
import com.ipaye.box_delivery_service.enums.BoxState;
import com.ipaye.box_delivery_service.exception.BoxAlreadyExistsException;
import com.ipaye.box_delivery_service.exception.BoxNotFoundException;
import com.ipaye.box_delivery_service.repository.BoxRepository;
import com.ipaye.box_delivery_service.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}
