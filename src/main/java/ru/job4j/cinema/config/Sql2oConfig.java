package ru.job4j.cinema.config;

import org.sql2o.Sql2o;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.job4j.cinema.map.FilmMapper;
import ru.job4j.cinema.map.UserMapper;
import ru.job4j.cinema.repository.FilmRepository;
import ru.job4j.cinema.repository.Sql2oFilmRepository;
import ru.job4j.cinema.repository.UserRepository;
import ru.job4j.cinema.repository.Sql2oUserRepository;

import javax.sql.DataSource;

@Configuration
public class Sql2oConfig {

    @Bean
    public Sql2o sql2o(DataSource dataSource) {
        return new Sql2o(dataSource);
    }

    @Bean
    public FilmMapper filmMapper() {
        return new FilmMapper();
    }

    @Bean
    public FilmRepository filmRepository(Sql2o sql2o, FilmMapper filmMapper) {
        return new Sql2oFilmRepository(sql2o, filmMapper);
    }

    @Bean
    public UserMapper userMapper() {
        return new UserMapper();
    }

    @Bean
    public UserRepository userRepository(Sql2o sql2o, UserMapper userMapper) {
        return new Sql2oUserRepository(sql2o, userMapper);
    }
}
