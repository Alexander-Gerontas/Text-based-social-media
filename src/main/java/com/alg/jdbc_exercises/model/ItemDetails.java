package com.alg.jdbc_exercises.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "item_details")
public class ItemDetails {
    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "item_details_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_details_id_seq")
    private Long id;

    @Column(nullable = false)
    private String details;

    public ItemDetails(String details) {
        this.details = details;
    }
}
