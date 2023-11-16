package com.alg.jdbc_exercises.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "items")
@Setter
@Getter
@NoArgsConstructor
public class Item {
    @Id
    @SequenceGenerator(name = "items_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "items_id_seq")
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private List<ItemDetails> itemDetails;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private List<ItemProperties> itemProperties;

    public Item(String name, List<ItemDetails> itemDetails, List<ItemProperties> itemProperties) {
        this.name = name;
        this.itemDetails = itemDetails;
        this.itemProperties = itemProperties;
    }
}
