package com.ledgr.service;

import com.ledgr.entity.Transaction;
import com.ledgr.entity.TransactionType;
import com.ledgr.entity.User;
import com.ledgr.repository.BudgetRepository;
import com.ledgr.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DashboardService {

    private final TransactionRepository txRepo;
    private final BudgetRepository budgetRepo;

    public DashboardService(TransactionRepository txRepo, BudgetRepository budgetRepo) {
        this.txRepo = txRepo;
        this.budgetRepo = budgetRepo;
    }

    public BigDecimal totalIncome(User u) {
        return sumByType(u, TransactionType.INCOME);
    }

    public BigDecimal totalExpense(User u) {
        return sumByType(u, TransactionType.EXPENSE);
    }

    public BigDecimal totalBalance(User u) {
        return totalIncome(u).subtract(totalExpense(u));
    }

    public BigDecimal monthlySavings(User u) {
        LocalDate now = LocalDate.now();
        LocalDate start = now.withDayOfMonth(1);
        LocalDate end = now.withDayOfMonth(now.lengthOfMonth());

        List<Transaction> thisMonth = txRepo.findByUserAndDateBetween(u, start, end);

        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expense = BigDecimal.ZERO;

        for (Transaction t : thisMonth) {
            if (t.getType() == TransactionType.INCOME) {
                income = income.add(t.getAmount());
            } else {
                expense = expense.add(t.getAmount());
            }
        }

        return income.subtract(expense);
    }

    public BigDecimal monthlyExpenseSoFar(User u) {
        LocalDate now = LocalDate.now();
        LocalDate start = now.withDayOfMonth(1);
        LocalDate end = now.withDayOfMonth(now.lengthOfMonth());

        return txRepo.findByUserAndDateBetween(u, start, end).stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal currentBudgetAmount(User u) {
        LocalDate now = LocalDate.now();
        Optional<com.ledgr.entity.Budget> b = budgetRepo.findByUserAndMonthAndYear(u, now.getMonthValue(), now.getYear());
        return b.map(com.ledgr.entity.Budget::getAmount).orElse(BigDecimal.ZERO);
    }

    public List<Transaction> recentTransactions(User u) {
        return txRepo.findTop5ByUserOrderByDateDesc(u);
    }

    private BigDecimal sumByType(User u, TransactionType type) {
        return txRepo.findByUserOrderByDateDesc(u).stream()
                .filter(t -> t.getType() == type)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
