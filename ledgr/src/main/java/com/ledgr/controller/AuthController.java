package com.ledgr.controller;

import com.ledgr.dto.LoginForm;
import com.ledgr.dto.RegisterForm;
import com.ledgr.entity.User;
import com.ledgr.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegister(Model model, HttpSession session) {
        if (session.getAttribute("userId") != null) {
            return "redirect:/dashboard";
        }
        if (!model.containsAttribute("registerForm")) {
            model.addAttribute("registerForm", new RegisterForm());
        }
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@Valid @ModelAttribute("registerForm") RegisterForm form, BindingResult result, Model model, HttpSession session) {

        if (!result.hasErrors() && !form.getPassword().equals(form.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "mismatch", "passwords don't match");
        }

        if (!result.hasErrors() && userService.emailTaken(form.getEmail().trim().toLowerCase())) {
            result.rejectValue("email", "taken", "an account with this email already exists");
        }

        if (result.hasErrors()) {
            return "register";
        }

        User newUser = userService.register(form.getName(), form.getEmail(), form.getPassword());

        session.setAttribute("userId", newUser.getId());
        session.setAttribute("userName", newUser.getName());

        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String showLogin(Model model, HttpSession session) {
        if (session.getAttribute("userId") != null) {
            return "redirect:/dashboard";
        }
        if (!model.containsAttribute("loginForm")) {
            model.addAttribute("loginForm", new LoginForm());
        }
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@ModelAttribute("loginForm") LoginForm form, Model model, HttpSession session) {

        Optional<User> maybeUser = userService.tryLogin(form.getEmail(), form.getPassword());

        if (maybeUser.isEmpty()) {
            model.addAttribute("loginError", "wrong email or password");
            return "login";
        }

        User u = maybeUser.get();
        session.setAttribute("userId", u.getId());
        session.setAttribute("userName", u.getName());

        return "redirect:/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
