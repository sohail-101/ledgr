package com.ledgr.service;

import com.ledgr.entity.Budget;
import com.ledgr.entity.Transaction;
import com.ledgr.entity.TransactionType;
import com.ledgr.entity.User;
import com.ledgr.repository.BudgetRepository;
import com.ledgr.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepo;
    private final TransactionRepository txRepo;

    public BudgetService(BudgetRepository budgetRepo, TransactionRepository txRepo) {
        this.budgetRepo = budgetRepo;
        this.txRepo = txRepo;
    }

    public Budget upsert(User user, int month, int year, BigDecimal amount) {
        Optional<Budget> existing = budgetRepo.findByUserAndMonthAndYear(user, month, year);

        if (existing.isPresent()) {
            Budget b = existing.get();
            b.setAmount(amount);
            return budgetRepo.save(b);
        }

        Budget b = new Budget(month, year, amount, user);
        return budgetRepo.save(b);
    }

    public BigDecimal spentFor(User user, int month, int year) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        return txRepo.findByUserAndDateBetween(user, start, end).stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<BudgetRow> historyFor(User user) {
        return user.getBudgets().stream()
                .sorted(Comparator.comparing(Budget::getYear).thenComparing(Budget::getMonth).reversed())
                .map(b -> {
                    BigDecimal spent = spentFor(user, b.getMonth(), b.getYear());
                    int pct = 0;
                    if (b.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                        pct = spent.multiply(BigDecimal.valueOf(100))
                                .divide(b.getAmount(), 0, RoundingMode.HALF_UP)
                                .intValue();
                    }
                    return new BudgetRow(b, spent, pct);
                })
                .toList();
    }
}
