package com.ipaye.box_delivery_service.config;

import com.ipaye.box_delivery_service.entity.Box;
import com.ipaye.box_delivery_service.enums.BoxState;
import com.ipaye.box_delivery_service.repository.BoxRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initializeDatabase(BoxRepository boxRepository){

        return args -> {
            if (boxRepository.count() > 0) {

                return;
            }

            boxRepository.save(new Box(
                    null,
                    "BOX001",
                    500,
                    85,
                     BoxState.IDLE,
                    null
            ));

            boxRepository.save(new Box(
                    null,
                    "BOX002",
                    400,
                    60,
                     BoxState.IDLE,
                    null
            ));

            boxRepository.save(new Box(
                    null,
                    "BOX003",
                    300,
                    20,
                     BoxState.IDLE,
                    null
            ));

            boxRepository.save(new Box(
                    null,
                    "BOX004",
                    500,
                    90,
                     BoxState.LOADED,
                    null
            ));
        };
    }
}
