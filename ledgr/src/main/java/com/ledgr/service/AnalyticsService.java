package com.ledgr.service;

import com.ledgr.entity.Category;
import com.ledgr.entity.Transaction;
import com.ledgr.entity.TransactionType;
import com.ledgr.entity.User;
import com.ledgr.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
public class AnalyticsService {

    private final TransactionRepository txRepo;

    public AnalyticsService(TransactionRepository txRepo) {
        this.txRepo = txRepo;
    }

    public Map<Category, BigDecimal> categoryBreakdown(User user) {
        Map<Category, BigDecimal> byCategory = new LinkedHashMap<>();

        List<Transaction> txList = txRepo.findByUserOrderByDateDesc(user);

        for (Transaction t : txList) {
            if (t.getType() != TransactionType.EXPENSE) {
                continue;
            }
            byCategory.merge(t.getCategory(), t.getAmount(), BigDecimal::add);
        }

        return byCategory;
    }

    public List<MonthlyPoint> monthlyTrend(User user, int monthsBack) {
        List<Transaction> txList = txRepo.findByUserOrderByDateDesc(user);

        YearMonth now = YearMonth.now();
        LinkedHashMap<YearMonth, BigDecimal[]> buckets = new LinkedHashMap<>();

        for (int i = monthsBack - 1; i >= 0; i--) {
            buckets.put(now.minusMonths(i), new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
        }

        for (Transaction t : txList) {
            YearMonth ym = YearMonth.from(t.getDate());
            BigDecimal[] bucket = buckets.get(ym);
            if (bucket == null) {
                continue;
            }
            if (t.getType() == TransactionType.INCOME) {
                bucket[0] = bucket[0].add(t.getAmount());
            } else {
                bucket[1] = bucket[1].add(t.getAmount());
            }
        }

        return buckets.entrySet().stream()
                .map(e -> new MonthlyPoint(
                        e.getKey().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + e.getKey().getYear(),
                        e.getValue()[0],
                        e.getValue()[1]))
                .toList();
    }

    public Optional<Category> largestExpenseCategory(User user) {
        return categoryBreakdown(user).entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    public Optional<String> highestSpendingMonth(User user) {
        return monthlyTrend(user, 24).stream()
                .filter(p -> p.getExpense().compareTo(BigDecimal.ZERO) > 0)
                .max((a, b) -> a.getExpense().compareTo(b.getExpense()))
                .map(MonthlyPoint::getLabel);
    }

    public BigDecimal totalIncome(User user) {
        return sumByType(user, TransactionType.INCOME);
    }

    public BigDecimal totalExpense(User user) {
        return sumByType(user, TransactionType.EXPENSE);
    }

    private BigDecimal sumByType(User user, TransactionType type) {
        return txRepo.findByUserOrderByDateDesc(user).stream()
                .filter(t -> t.getType() == type)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
