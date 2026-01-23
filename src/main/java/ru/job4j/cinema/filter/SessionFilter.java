package ru.job4j.cinema.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.job4j.cinema.model.User;

import java.io.IOException;

@Component
@Order(1)
public class SessionFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain chain) throws IOException, ServletException {

        HttpSession session = request.getSession();

        User user = (User) session.getAttribute("user");

        if (user == null) {
            User guest = new User();
            guest.setFullName("Гость");
            guest.setEmail(null);
            session.setAttribute("user", guest);
            request.setAttribute("user", guest);
        } else {
            request.setAttribute("user", user);
        }

        chain.doFilter(request, response);
    }
}
