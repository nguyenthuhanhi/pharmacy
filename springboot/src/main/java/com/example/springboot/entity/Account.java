package com.example.springboot.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.*;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Integer accountId;

    @Column(name="email", columnDefinition = "nvarchar(150)", unique = true)
    private String email;

    private String password;

    @Column(name="delete_flag")
    private Boolean deleteFlag;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    List<AccountRole> accountRoles;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, optional = true, mappedBy = "account")
    private Customer customer;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, optional = true, mappedBy = "account")
    private Employee employee;

}
