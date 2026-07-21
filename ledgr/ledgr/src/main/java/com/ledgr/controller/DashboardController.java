package com.ledgr.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ledgr.entity.Category;
import com.ledgr.entity.User;
import com.ledgr.repository.UserRepository;
import com.ledgr.service.AnalyticsService;
import com.ledgr.service.DashboardService;

import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController {

    private final UserRepository userRepo;
    private final DashboardService dashboardService;
    private final AnalyticsService analyticsService;

    public DashboardController(UserRepository userRepo, DashboardService dashboardService, AnalyticsService analyticsService) {
        this.userRepo = userRepo;
        this.dashboardService = dashboardService;
        this.analyticsService = analyticsService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long userId = Objects.requireNonNull((Long) session.getAttribute("userId"), "userId is required in session");
        User u = userRepo.findById(userId).orElseThrow();

        BigDecimal budget = dashboardService.currentBudgetAmount(u);
        BigDecimal spentThisMonth = dashboardService.monthlyExpenseSoFar(u);

        int pctSpent = 0;
        if (budget.compareTo(BigDecimal.ZERO) > 0) {
            pctSpent = spentThisMonth.multiply(BigDecimal.valueOf(100))
                    .divide(budget, 0, RoundingMode.HALF_UP)
                    .intValue();
        }
        if (pctSpent > 100) {
            pctSpent = 100;
        }

        model.addAttribute("user", u);
        model.addAttribute("totalBalance", dashboardService.totalBalance(u));
        model.addAttribute("totalIncome", dashboardService.totalIncome(u));
        model.addAttribute("totalExpense", dashboardService.totalExpense(u));
        model.addAttribute("monthlySavings", dashboardService.monthlySavings(u));
        Map<Category, BigDecimal> categoryBreakdown = analyticsService.categoryBreakdown(u);

        model.addAttribute("budgetAmount", budget);
        model.addAttribute("spentThisMonth", spentThisMonth);
        model.addAttribute("pctSpent", pctSpent);
        model.addAttribute("recentTx", dashboardService.recentTransactions(u));
        model.addAttribute("hasCategoryData", !categoryBreakdown.isEmpty());
        model.addAttribute("categoryLabels", categoryBreakdown.keySet().stream().map(category -> category.name()).toList());
        model.addAttribute("categoryValues", categoryBreakdown.values().stream().map(amount -> amount.doubleValue()).toList());

        return "dashboard";
    }
}
