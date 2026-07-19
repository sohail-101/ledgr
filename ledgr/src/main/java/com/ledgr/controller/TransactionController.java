package com.ledgr.controller;

import com.ledgr.dto.TransactionForm;
import com.ledgr.entity.Category;
import com.ledgr.entity.Transaction;
import com.ledgr.entity.TransactionType;
import com.ledgr.entity.User;
import com.ledgr.repository.UserRepository;
import com.ledgr.service.TransactionService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    private final UserRepository userRepo;
    private final TransactionService txService;

    public TransactionController(UserRepository userRepo, TransactionService txService) {
        this.userRepo = userRepo;
        this.txService = txService;
    }

    @GetMapping
    public String list(HttpSession session, Model model,
                        @RequestParam(required = false) String category,
                        @RequestParam(required = false) String type,
                        @RequestParam(required = false) String q) {

        User user = currentUser(session);
        List<Transaction> txList = txService.allFor(user);

        if (category != null && !category.isBlank()) {
            txList = txList.stream().filter(t -> t.getCategory().name().equals(category)).toList();
        }
        if (type != null && !type.isBlank()) {
            txList = txList.stream().filter(t -> t.getType().name().equals(type)).toList();
        }
        if (q != null && !q.isBlank()) {
            String needle = q.trim().toLowerCase();
            txList = txList.stream()
                    .filter(t -> t.getDescription() != null && t.getDescription().toLowerCase().contains(needle))
                    .toList();
        }

        model.addAttribute("user", user);
        model.addAttribute("txList", txList);
        model.addAttribute("categories", Category.values());
        model.addAttribute("types", TransactionType.values());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedType", type);
        model.addAttribute("q", q);

        if (!model.containsAttribute("txForm")) {
            model.addAttribute("txForm", new TransactionForm());
        }

        return "transactions";
    }

    @PostMapping
    public String add(@Valid @ModelAttribute("txForm") TransactionForm form, BindingResult result,
                       HttpSession session, Model model) {

        User user = currentUser(session);

        if (result.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("txList", txService.allFor(user));
            model.addAttribute("categories", Category.values());
            model.addAttribute("types", TransactionType.values());
            model.addAttribute("showAddErrors", true);
            return "transactions";
        }

        txService.create(user, form);
        return "redirect:/transactions";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, HttpSession session, Model model) {
        User user = currentUser(session);
        Transaction tx = txService.getOwned(user, id);

        TransactionForm form = new TransactionForm();
        form.setAmount(tx.getAmount());
        form.setDescription(tx.getDescription());
        form.setCategory(tx.getCategory().name());
        form.setType(tx.getType().name());
        form.setDate(tx.getDate());

        model.addAttribute("user", user);
        model.addAttribute("txForm", form);
        model.addAttribute("txId", tx.getId());
        model.addAttribute("categories", Category.values());
        model.addAttribute("types", TransactionType.values());

        return "transaction-edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("txForm") TransactionForm form,
                          BindingResult result, HttpSession session, Model model) {

        User user = currentUser(session);

        if (result.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("txId", id);
            model.addAttribute("categories", Category.values());
            model.addAttribute("types", TransactionType.values());
            return "transaction-edit";
        }

        txService.update(user, id, form);
        return "redirect:/transactions";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session) {
        User user = currentUser(session);
        txService.delete(user, id);
        return "redirect:/transactions";
    }

    private User currentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        return userRepo.findById(userId).orElseThrow();
    }
}
