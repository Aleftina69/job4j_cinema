package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.job4j.cinema.dto.RegistrationForm;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private UserService userService;
    private UserController userController;
    private HttpServletRequest request;
    private HttpSession session;
    private RedirectAttributes redirectAttributes;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
        request = mock(HttpServletRequest.class);
        session = mock(HttpSession.class);
        redirectAttributes = mock(RedirectAttributes.class);

        when(request.getSession()).thenReturn(session);
    }

    @Test
    void whenDoLoginSuccessfullyThenRedirectToSessions() {
        String email = "test@mail.com";
        String password = "123";
        User user = new User();
        user.setId(1);
        user.setFullName("Test User");
        user.setEmail(email);
        user.setPassword(password);

        when(userService.findByEmailAndPassword(email, password)).thenReturn(Optional.of(user));

        String result = userController.doLogin(email, password, request, redirectAttributes);

        assertEquals("redirect:/sessions", result);
        verify(session).setAttribute("user", user);
    }

    @Test
    void whenDoLoginFailedThenRedirectToLoginWithError() {
        String email = "wrong@mail.com";
        String password = "321";

        when(userService.findByEmailAndPassword(email, password)).thenReturn(Optional.empty());

        String result = userController.doLogin(email, password, request, redirectAttributes);

        assertEquals("redirect:/login", result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
    }

    @Test
    void whenLogoutThenSessionInvalidatedAndRedirectToLogin() {
        String result = userController.logout(request, redirectAttributes);

        assertEquals("redirect:/login", result);
        verify(session).invalidate();
        verify(redirectAttributes).addFlashAttribute(eq("message"), anyString());
    }

    @Test
    void whenDoRegisterSuccessfullyThenRedirectToLoginWithMessage() {
        RegistrationForm form = new RegistrationForm();
        form.setFullName("New User");
        form.setEmail("new@mail.com");
        form.setPassword("123");

        User user = new User();
        user.setId(2);
        user.setFullName(form.getFullName());
        user.setEmail(form.getEmail());
        user.setPassword(form.getPassword());

        when(userService.register(any(RegistrationForm.class))).thenReturn(Optional.of(user));

        String result = userController.doRegister(
                form.getFullName(),
                form.getEmail(),
                form.getPassword(),
                redirectAttributes
        );

        assertEquals("redirect:/login", result);
        verify(redirectAttributes).addFlashAttribute(eq("message"), anyString());
    }

    @Test
    void whenDoRegisterFailedThenRedirectToRegisterWithError() {
        RegistrationForm form = new RegistrationForm();
        form.setFullName("New User");
        form.setEmail("existing@mail.com");
        form.setPassword("123");

        when(userService.register(any(RegistrationForm.class))).thenReturn(Optional.empty());

        String result = userController.doRegister(
                form.getFullName(),
                form.getEmail(),
                form.getPassword(),
                redirectAttributes
        );

        assertEquals("redirect:/register", result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
    }
}
