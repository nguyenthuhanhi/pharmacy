package com.example.springboot.entity;
import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ware_in_detail")
public class WarehouseInDetail {
    @Id
    @Column(name = "ware_in_detail_id")
    private String wareInDetailId;

    @ManyToOne(cascade = {
            CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "medicine_id", referencedColumnName = "medicine_id")
    private Medicine medicine;

    private String unit;

    private int quantity;

    private int batchNumber;

    private Date expiredDate;

    private Float price;

    @ManyToOne
    @JoinColumn(name = "warehouse_in_id", referencedColumnName = "warehouse_in_id")
    private WarehouseIn warehouseIn;
}