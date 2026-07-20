package com.ledgr.controller;

import com.ledgr.dto.DeleteAccountForm;
import com.ledgr.dto.NameForm;
import com.ledgr.dto.PasswordForm;
import com.ledgr.entity.User;
import com.ledgr.repository.UserRepository;
import com.ledgr.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserRepository userRepo;
    private final UserService userService;

    public ProfileController(UserRepository userRepo, UserService userService) {
        this.userRepo = userRepo;
        this.userService = userService;
    }

    @GetMapping
    public String page(HttpSession session, Model model) {
        User user = currentUser(session);

        model.addAttribute("user", user);

        if (!model.containsAttribute("nameForm")) {
            NameForm nf = new NameForm();
            nf.setName(user.getName());
            model.addAttribute("nameForm", nf);
        }
        if (!model.containsAttribute("passwordForm")) {
            model.addAttribute("passwordForm", new PasswordForm());
        }
        if (!model.containsAttribute("deleteForm")) {
            model.addAttribute("deleteForm", new DeleteAccountForm());
        }

        return "profile";
    }

    @PostMapping("/name")
    public String updateName(@Valid @ModelAttribute("nameForm") NameForm form, BindingResult result,
                              HttpSession session, Model model) {
        User user = currentUser(session);

        if (result.hasErrors()) {
            return reload(user, model);
        }

        User updated = userService.updateName(user, form.getName());
        session.setAttribute("userName", updated.getName());

        model.addAttribute("nameSaved", true);
        return reload(updated, model);
    }

    @PostMapping("/password")
    public String changePassword(@Valid @ModelAttribute("passwordForm") PasswordForm form, BindingResult result,
                                  HttpSession session, Model model) {
        User user = currentUser(session);

        if (!result.hasErrors() && !userService.checkPassword(user, form.getCurrentPassword())) {
            result.rejectValue("currentPassword", "wrong", "current password is wrong");
        }
        if (!result.hasErrors() && !form.getNewPassword().equals(form.getConfirmNewPassword())) {
            result.rejectValue("confirmNewPassword", "mismatch", "passwords don't match");
        }

        if (result.hasErrors()) {
            return reload(user, model);
        }

        userService.changePassword(user, form.getNewPassword());
        model.addAttribute("passwordSaved", true);
        return reload(user, model);
    }

    @PostMapping("/delete")
    public String deleteAccount(@Valid @ModelAttribute("deleteForm") DeleteAccountForm form, BindingResult result,
                                 HttpSession session, Model model) {
        User user = currentUser(session);

        if (!result.hasErrors() && !userService.checkPassword(user, form.getPassword())) {
            result.rejectValue("password", "wrong", "password is wrong");
        }

        if (result.hasErrors()) {
            return reload(user, model);
        }

        userService.deleteAccount(user);
        session.invalidate();
        return "redirect:/";
    }

    private String reload(User user, Model model) {
        model.addAttribute("user", user);
        if (!model.containsAttribute("nameForm")) {
            NameForm nf = new NameForm();
            nf.setName(user.getName());
            model.addAttribute("nameForm", nf);
        }
        if (!model.containsAttribute("passwordForm")) {
            model.addAttribute("passwordForm", new PasswordForm());
        }
        if (!model.containsAttribute("deleteForm")) {
            model.addAttribute("deleteForm", new DeleteAccountForm());
        }
        return "profile";
    }

    private User currentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        return userRepo.findById(userId).orElseThrow();
    }
}
