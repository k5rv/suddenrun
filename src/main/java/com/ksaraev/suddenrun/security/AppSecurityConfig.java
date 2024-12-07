package com.ksaraev.suddenrun.security;

import static org.springframework.http.HttpMethod.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile(value = {"development", "production", "kube", "eks"})
public class AppSecurityConfig {

  private static final String API_V1_USERS = "/api/v1/users/**";

  private static final String API_V1_USERS_CURRENT = "/api/v1/users/current";

  private static final String API_V1_PLAYLISTS = "/api/v1/playlists/**";

  private static final String ACTUATOR_HEALTH = "/actuator/health";

  @Bean
  public SecurityFilterChain oauth2FilterChain(HttpSecurity http) throws Exception {
    return http.csrf()
        .disable()
        .authorizeHttpRequests(
            authorize -> authorize.requestMatchers(GET, ACTUATOR_HEALTH).permitAll())
        .authorizeHttpRequests(authorize -> authorize.requestMatchers(GET, "/*").authenticated())
        .authorizeHttpRequests(
            authorize -> authorize.requestMatchers(GET, API_V1_USERS_CURRENT).authenticated())
        .authorizeHttpRequests(
            authorize -> authorize.requestMatchers(GET, API_V1_USERS).authenticated())
        .authorizeHttpRequests(
            authorize -> authorize.requestMatchers(POST, API_V1_USERS).authenticated())
        .authorizeHttpRequests(
            authorize -> authorize.requestMatchers(GET, API_V1_PLAYLISTS).authenticated())
        .authorizeHttpRequests(
            authorize -> authorize.requestMatchers(POST, API_V1_PLAYLISTS).authenticated())
        .authorizeHttpRequests(
            authorize -> authorize.requestMatchers(PUT, API_V1_PLAYLISTS).authenticated())
        .oauth2Login(Customizer.withDefaults())
        .build();
  }
}
