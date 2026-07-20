package com.ledgr.controller;

import com.ledgr.entity.User;
import com.ledgr.repository.UserRepository;
import com.ledgr.service.DashboardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Controller
public class DashboardController {

    private final UserRepository userRepo;
    private final DashboardService dashboardService;

    public DashboardController(UserRepository userRepo, DashboardService dashboardService) {
        this.userRepo = userRepo;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
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
        model.addAttribute("budgetAmount", budget);
        model.addAttribute("spentThisMonth", spentThisMonth);
        model.addAttribute("pctSpent", pctSpent);
        model.addAttribute("recentTx", dashboardService.recentTransactions(u));

        return "dashboard";
    }
}
