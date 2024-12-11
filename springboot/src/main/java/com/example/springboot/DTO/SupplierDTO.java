package com.codegym.a0223i1_pharmacy_professional_be.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SupplierDTO {
    private String supplierId;
    private String supplierName;
    private String address;
    private String email;
    private String phoneNumber;
    private String note;

    private int toPayDebt;
    private Boolean deleteFlag;

    public SupplierDTO(String supplierId, String supplierName, String address, String email, String phoneNumber, String note) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.note = note;
    }

}