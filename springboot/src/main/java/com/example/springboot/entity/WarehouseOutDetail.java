package com.example.springboot.entity;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "warehouse_out_detail")
public class WarehouseOutDetail {
    @Id
    @Column(name = "ware_out_detail_id")
    private String wareOutDetailId;

    @ManyToOne(cascade = {
            CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "medicine_id", referencedColumnName = "medicine_id")
    private Medicine medicine;

    private String unit;

    private int quantity;

    private Date expiredDate;

    private Float price;

    @ManyToOne
    @JoinColumn(name = "warehouse_out_id", referencedColumnName = "warehouse_out_id")
    private WarehouseOut warehouseOut;
}
