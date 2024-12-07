package com.example.springboot.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "supplier")
public class Supplier {
    @Id
    @Column(name = "supplier_id")
    private String supplierId;

    @Column(columnDefinition = "TEXT")
    private String supplierName;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(columnDefinition = "TEXT")
    private String email;

    @Column(columnDefinition = "TEXT")
    private String phoneNumber;


    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "to_pay_debt")
    private int toPayDebt;

    @Column(name = "delete_flag")
    private Boolean deleteFlag;

    @JsonBackReference
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL)
    List<WarehouseIn> warehouseIns;

    @JsonBackReference
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL)
    Set<WarehouseOut> warehouseOuts;

    public Supplier(String supplierId, String supplierName, String address, String email, String phoneNumber, String note) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.note = note;
    }

    public Supplier(String supplierId, String supplierName, String address, String email, String phoneNumber, String note, int toPayDebt, Boolean deleteFlag) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.note = note;
        this.toPayDebt = toPayDebt;
        this.deleteFlag = deleteFlag;
    }

}