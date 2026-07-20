package com.ledgr.repository;

import com.ledgr.entity.Budget;
import com.ledgr.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Optional<Budget> findByUserAndMonthAndYear(User user, int month, int year);
}
