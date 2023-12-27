package com.unitech.banking.repository;

import com.unitech.banking.model.entity.Account;
import com.unitech.banking.model.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    @Query("select a from Account a where a.client=:client and a.status='ACTIVE'")
    List<Account> getActiveAccountList(Client client);

    Optional<Account> getByIdAndClient(String id, Client client);
}
