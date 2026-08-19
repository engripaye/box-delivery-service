package com.ipaye.box_delivery_service.service;

import com.ipaye.box_delivery_service.dto.*;

import java.util.List;

public interface BoxService {

    BoxResponse createBox(CreateBoxRequest createBoxRequest);

    List<BoxResponse> getAvailableBoxes();

    List<ItemResponse> loadItems(String txref, List<ItemRequest> items);

    List<ItemResponse> getLoadedItems(String txref);

    BatteryResponse getBatteryCapacity(String txref);
}
