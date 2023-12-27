package com.unitech.banking.repository;

import com.unitech.banking.model.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByPin(String pin);
    boolean existsByPin(String pin);
}
