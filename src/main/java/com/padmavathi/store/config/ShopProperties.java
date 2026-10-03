package com.padmavathi.store.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "shop")
@Getter
@Setter
public class ShopProperties {

    private String whatsappNumber;
}