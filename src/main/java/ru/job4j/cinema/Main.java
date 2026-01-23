package ru.job4j.cinema;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.job4j.cinema.dto.TicketPurchaseForm;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        TicketPurchaseForm form = new TicketPurchaseForm();
        form.setRowNumber(10);
        form.setPlaceNumber(5);
        form.setSessionId(123);

        SpringApplication.run(Main.class, args);
    }
}