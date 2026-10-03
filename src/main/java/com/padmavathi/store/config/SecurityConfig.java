package com.padmavathi.store.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

		return configuration.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http)
		throws Exception {
			http
				// =========================
				// CSRF
				// =========================

				.csrf(AbstractHttpConfigurer::disable)

				// =========================
				// JWT - STATELESS
				// =========================

				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				// =========================
				// AUTHORIZATION
				// =========================

				.authorizeHttpRequests(auth -> auth

						// -------------------------
						// CUSTOMER WEBSITE
						// -------------------------

						// Homepage
						.requestMatchers("/").permitAll()

						// Thymeleaf pages
						.requestMatchers("/index.html", "/admin", "/admin.html").permitAll()

						// Static resources
						.requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**", "/favicon.ico").permitAll()

						// -------------------------
						// PUBLIC APIs
						// -------------------------

						// Render health check
						.requestMatchers("/actuator/health/**").permitAll()

						// Admin login
						.requestMatchers("/api/auth/**").permitAll()

						// Customers can view products
						.requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()

						// Customers can view categories
						.requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()

						// Customers can place orders
						.requestMatchers(HttpMethod.POST, "/api/orders").permitAll()

						// Shop information
						.requestMatchers("/api/shop/**").permitAll()

						// -------------------------
						// ADMIN APIs
						// -------------------------

						// Product management
						.requestMatchers("/api/products/**").hasRole("ADMIN")

						// Category management
						.requestMatchers("/api/categories/**").hasRole("ADMIN")

						// Photo uploads
						.requestMatchers("/api/uploads/**").hasRole("ADMIN")

						// Order management
						.requestMatchers("/api/orders/**").hasRole("ADMIN")

						// -------------------------
						// EVERYTHING ELSE
						// -------------------------

						.anyRequest().authenticated())

				// =========================
				// JWT FILTER
				// =========================

				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}