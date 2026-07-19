package com.ledgr.repository;

import com.ledgr.entity.Transaction;
import com.ledgr.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserOrderByDateDesc(User user);

    List<Transaction> findTop5ByUserOrderByDateDesc(User user);

    List<Transaction> findByUserAndDateBetween(User user, LocalDate start, LocalDate end);
}
