package com.unitech.banking.model.entity;

import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CLIENT")
@Entity
public class Client {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "PIN", unique = true)
    private String pin;

    @Column(name = "PASSWORD")
    private String password;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Account> accountList;
}
