package com.padmavathi.store.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.padmavathi.store.entity.AdminUser;
import com.padmavathi.store.repository.AdminUserRepository;

@Component
public class AdminUserInitializer implements CommandLineRunner {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserInitializer(
            AdminUserRepository adminUserRepository,
            PasswordEncoder passwordEncoder) {

        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Value("${app.admin.initial-password:Admin@123}")
    private String initialPassword;

    @Override
    public void run(String... args) {
        if (adminUserRepository.findByUsername("admin").isPresent()) {
            return; // admin already exists — leave the password alone
        }

        AdminUser adminUser = new AdminUser();
        adminUser.setUsername("admin");
        adminUser.setPasswordHash(passwordEncoder.encode(initialPassword));
        adminUser.setRole("ADMIN");
        adminUserRepository.save(adminUser);

        System.out.println("=================================");
        System.out.println("Admin user created — username: admin");
        System.out.println("Log in and change the password right away.");
        System.out.println("=================================");
    }
}