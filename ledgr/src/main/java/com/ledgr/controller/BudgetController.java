package com.ledgr.controller;

import com.ledgr.dto.BudgetForm;
import com.ledgr.entity.User;
import com.ledgr.repository.UserRepository;
import com.ledgr.service.BudgetService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

@Controller
public class BudgetController {

    private final UserRepository userRepo;
    private final BudgetService budgetService;

    public BudgetController(UserRepository userRepo, BudgetService budgetService) {
        this.userRepo = userRepo;
        this.budgetService = budgetService;
    }

    @GetMapping("/budgets")
    public String page(HttpSession session, Model model) {
        User user = currentUser(session);
        loadCommonAttrs(user, model);

        if (!model.containsAttribute("budgetForm")) {
            LocalDate now = LocalDate.now();
            BudgetForm form = new BudgetForm();
            form.setMonth(now.getMonthValue());
            form.setYear(now.getYear());
            model.addAttribute("budgetForm", form);
        }

        return "budgets";
    }

    @PostMapping("/budgets")
    public String save(@Valid @ModelAttribute("budgetForm") BudgetForm form, BindingResult result,
                        HttpSession session, Model model) {

        User user = currentUser(session);

        if (result.hasErrors()) {
            loadCommonAttrs(user, model);
            return "budgets";
        }

        budgetService.upsert(user, form.getMonth(), form.getYear(), form.getAmount());
        return "redirect:/budgets";
    }

    private void loadCommonAttrs(User user, Model model) {
        LocalDate now = LocalDate.now();
        BigDecimal spentThisMonth = budgetService.spentFor(user, now.getMonthValue(), now.getYear());

        model.addAttribute("user", user);
        model.addAttribute("spentThisMonth", spentThisMonth);
        model.addAttribute("history", budgetService.historyFor(user));
        model.addAttribute("currentMonth", now.getMonthValue());
        model.addAttribute("currentYear", now.getYear());
        model.addAttribute("monthNames", monthNames());
    }

    private List<String> monthNames() {
        return Arrays.stream(Month.values())
                .map(m -> m.name().charAt(0) + m.name().substring(1).toLowerCase())
                .toList();
    }

    private User currentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        return userRepo.findById(userId).orElseThrow();
    }
}
