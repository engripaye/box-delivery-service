package com.ipaye.box_delivery_service.entity;

import com.ipaye.box_delivery_service.enums.BoxState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Entity
@Table( name = "boxes" )
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Box {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String txref;

    @Column(nullable = false)
    private Integer weightLimit;

    @Column(nullable = false)
    private Integer batteryCapacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoxState state;

    @OneToMany(
            mappedBy = "box",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Item> items =  new ArrayList<>();

}
