package net.ukr.kondrashev;

import net.ukr.kondrashev.entities.*;
import net.ukr.kondrashev.repositories.CustomUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class DictionaryApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(DictionaryApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner(final CustomUserRepository customUserRepository) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) {
                customUserRepository.save(new CustomUser("admin", "d033e22ae348aeb5660fc2140aec35850c4da997", UserRole.ADMIN));
            }
        };
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/");
    }
}
