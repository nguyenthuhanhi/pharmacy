
package com.example.springboot.entity;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invoice_pres")
public class InvoicePres {
    @Id
    @Column(name = "invoice_pres_id")
    private String invoicePresId;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "employee_id")
    private Employee employee;

    @Column(name = "date_create",columnDefinition = "DATETIME")
    private Timestamp dateCreated;

    private String status;

    private String doctorName;

    private String symptom;

    private String doctorPhone;

    private String doctorDiagnosis;

    private String note;

    private Float total;

    @OneToMany(mappedBy = "invoicePres", cascade = CascadeType.ALL)
    List<InvoiceDetail> invoiceDetails;
}
