package com.padmavathi.store.controller;

import com.padmavathi.store.config.ShopProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ShopController {

    private final ShopProperties shopProperties;

    @GetMapping("/contact")
    public Map<String, String> getContact() {
        return Map.of(
                "whatsappNumber",
                shopProperties.getWhatsappNumber()
        );
    }
}