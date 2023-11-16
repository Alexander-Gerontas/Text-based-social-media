package com.alg.jdbc_exercises.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "item_properties")
public class ItemProperties {
    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "item_properties_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_properties_id_seq")
    private Long id;

    @Column(nullable = false)
    private String properties;

    public ItemProperties(String properties) {
        this.properties = properties;
    }
}
