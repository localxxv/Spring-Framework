package org.example.carrent.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class ProfileLogger implements CommandLineRunner {

    private final Environment environment;

    public ProfileLogger(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(String... args) {
        System.out.println("ACTIVE SPRING PROFILES: " + Arrays.toString(environment.getActiveProfiles()));

        for (String profile : environment.getActiveProfiles()) {
            if (profile.equals("jdbc")) {
                System.out.println("Tryb repozytoriów: JDBC");
            }

            if (profile.equals("jpa")) {
                System.out.println("Tryb repozytoriów: JPA/HIBERNATE");
            }
        }
    }
}