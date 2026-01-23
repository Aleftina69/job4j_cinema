package ru.job4j.cinema.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.job4j.cinema.dto.RegistrationForm;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.UserService;

import java.util.Optional;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("message") String message,
                            @ModelAttribute("error") String error,
                            Model model) {
        if (!message.isEmpty()) {
            model.addAttribute("message", message);
        }
        if (!error.isEmpty()) {
            model.addAttribute("error", error);
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String email,
                          @RequestParam String password,
                          HttpServletRequest request,
                          RedirectAttributes redirectAttributes) {

        Optional<User> userOptional = userService.findByEmailAndPassword(email, password);

        if (userOptional.isPresent()) {
            request.getSession().setAttribute("user", userOptional.get());
            return "redirect:/sessions";
        } else {
            redirectAttributes.addFlashAttribute("error", "Неверный email или пароль.");
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        request.getSession().invalidate();
        redirectAttributes.addFlashAttribute("message", "Вы вышли из системы.");
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String registerForm(@ModelAttribute("message") String message,
                               @ModelAttribute("error") String error,
                               Model model) {
        if (!message.isEmpty()) {
            model.addAttribute("message", message);
        }
        if (!error.isEmpty()) {
            model.addAttribute("error", error);
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String doRegister(@RequestParam String fullName,
                             @RequestParam String email,
                             @RequestParam String password,
                             RedirectAttributes redirectAttributes) {
        RegistrationForm form = new RegistrationForm();
        form.setFullName(fullName);
        form.setEmail(email);
        form.setPassword(password);

        Optional<User> registeredUser = userService.register(form);

        if (registeredUser.isPresent()) {
            redirectAttributes.addFlashAttribute("message", "Регистрация успешна! Теперь войдите.");
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("error", "Ошибка регистрации. Email уже занят.");
            return "redirect:/register";
        }
    }
}
