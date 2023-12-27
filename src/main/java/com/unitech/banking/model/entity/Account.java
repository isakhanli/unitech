package com.unitech.banking.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.unitech.banking.model.enums.AccountState;
import com.unitech.banking.model.enums.Currency;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ACCOUNT")
@Entity
public class Account {

    @Id
    @Column(name = "ID")
    private String id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "CLIENT_ID", nullable = false)
    private Client client;

    @Column(name = "AMOUNT", unique = true, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "CURRENCY", nullable = false)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private AccountState status;

    @JsonIgnore
    @Transient
    public boolean isActive(){
        return this.status.equals(AccountState.ACTIVE);
    }

    @JsonIgnore
    @Transient
    public boolean isTransferAmountWithinLimits(BigDecimal transferAmount){
        return this.amount.compareTo(transferAmount) >= 0;
    }

    @JsonIgnore
    @Transient
    public void add(BigDecimal amount){
        this.amount = this.amount.add(amount);
    }

    @JsonIgnore
    @Transient
    public void subtract(BigDecimal amount){
        this.amount = this.amount.subtract(amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account account)) return false;
        return Objects.equals(getId(), account.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
