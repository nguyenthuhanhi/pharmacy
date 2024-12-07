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
@Table(name = "customer")
public class Customer {
    @Id
    @Column(name = "customer_id")
    private String customerId;

    @Column(name="customer_name", columnDefinition = "nvarchar(250)")
    private String customerName;

    private Integer age;

    @Column(name="address", columnDefinition = "nvarchar(250)")
    private String address;

    @Column(name="phone_number", columnDefinition = "nvarchar(15)")
    private String phoneNumber;

    @Column(name="customer_type")
    private String customerType;

    private String note;

    @JsonBackReference
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", referencedColumnName = "account_id")
    private Account account;

    @JsonBackReference
    @OneToOne(cascade = CascadeType.ALL, optional = true, mappedBy = "customer")
    private Cart cart;

    @JsonBackReference
    @OneToMany(mappedBy = "customer")
    List<Invoice> invoices;
}
