package com.example.springboot.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "medicine_group")
public class MedicineGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medicine_group_id")
    private int medicineGroupId;

    private String medicineGroupName;

    @JsonIgnore
    @OneToMany(mappedBy = "medicineGroup", cascade = CascadeType.ALL)
    List<Medicine> medicines;
}