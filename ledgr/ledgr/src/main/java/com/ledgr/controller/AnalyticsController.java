package com.ledgr.controller;

import com.ledgr.entity.Category;
import com.ledgr.entity.User;
import com.ledgr.repository.UserRepository;
import com.ledgr.service.AnalyticsService;
import com.ledgr.service.MonthlyPoint;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller
public class AnalyticsController {

    private final UserRepository userRepo;
    private final AnalyticsService analyticsService;

    public AnalyticsController(UserRepository userRepo, AnalyticsService analyticsService) {
        this.userRepo = userRepo;
        this.analyticsService = analyticsService;
    }

    @GetMapping("/analytics")
    public String page(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        User user = userRepo.findById(userId).orElseThrow();

        Map<Category, BigDecimal> byCategory = analyticsService.categoryBreakdown(user);
        List<MonthlyPoint> trend = analyticsService.monthlyTrend(user, 6);

        model.addAttribute("user", user);
        model.addAttribute("totalIncome", analyticsService.totalIncome(user));
        model.addAttribute("totalExpense", analyticsService.totalExpense(user));

        model.addAttribute("categoryLabels", byCategory.keySet().stream().map(Enum::name).toList());
        model.addAttribute("categoryValues", byCategory.values().stream().map(BigDecimal::doubleValue).toList());
        model.addAttribute("hasCategoryData", !byCategory.isEmpty());

        model.addAttribute("trendLabels", trend.stream().map(MonthlyPoint::getLabel).toList());
        model.addAttribute("trendIncome", trend.stream().map(p -> p.getIncome().doubleValue()).toList());
        model.addAttribute("trendExpense", trend.stream().map(p -> p.getExpense().doubleValue()).toList());

        model.addAttribute("largestCategory", analyticsService.largestExpenseCategory(user).map(Enum::name).orElse(null));
        model.addAttribute("highestMonth", analyticsService.highestSpendingMonth(user).orElse(null));

        return "analytics";
    }
}
