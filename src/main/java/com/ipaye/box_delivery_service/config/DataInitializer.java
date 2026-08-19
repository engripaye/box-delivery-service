package com.ipaye.box_delivery_service.config;

import com.ipaye.box_delivery_service.entity.Box;
import com.ipaye.box_delivery_service.enums.BoxState;
import com.ipaye.box_delivery_service.repository.BoxRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initializeDatabase(BoxRepository boxRepository){

        return args -> {
            if (boxRepository.count() > 0) {
                return;
            }

            Box box1 = new Box();
            box1.setTxref("BOX001");
            box1.setWeightLimit(500);
            box1.setBatteryCapacity(85);
            box1.setState(BoxState.IDLE);

            Box box2 = new Box();
            box2.setTxref("BOX002");
            box2.setWeightLimit(400);
            box2.setBatteryCapacity(60);
            box2.setState(BoxState.IDLE);

            Box box3 = new Box();
            box3.setTxref("BOX003");
            box3.setWeightLimit(300);
            box3.setBatteryCapacity(20);
            box3.setState(BoxState.IDLE);

            Box box4 = new Box();
            box4.setTxref("BOX004");
            box4.setWeightLimit(500);
            box4.setBatteryCapacity(90);
            box4.setState(BoxState.LOADED);

            boxRepository.save(box1);
            boxRepository.save(box2);
            boxRepository.save(box3);
            boxRepository.save(box4);

        };
    }
}
