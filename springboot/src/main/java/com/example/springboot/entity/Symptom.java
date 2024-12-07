package com.example.springboot.entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "symptom")
public class Symptom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "symptom_id")
    private Integer symptomId;

    private String symptomName;

    @OneToMany(mappedBy = "symptom", cascade = CascadeType.ALL)
    private Set<Prescription> prescriptions;
}
