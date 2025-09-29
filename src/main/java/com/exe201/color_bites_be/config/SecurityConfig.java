package com.exe201.color_bites_be.config;

import com.exe201.color_bites_be.service.IAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    @Lazy
    private IAuthenticationService authenticationService;


//    @Autowired
//    private CustomOAuth2UserService oauth2UserService;
//
//    @Autowired
//    private OAuth2SuccessHandler successHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(req -> req
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/register",
                                "/api/loginByGoogle",
                                "/oauth2/authorization/**",
                                "/login/oauth2/code/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/api/vnpay-return",
                                "/api/payment/payos/webhook",
                                "/api/payment/payos/return"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
//                .oauth2Login(oauth2 -> oauth2
//                        .loginPage("http://localhost:5173/login") // Cập nhật cho FE local, Vercel có thể chỉnh env sau
//                        .userInfoEndpoint(userInfo -> userInfo.userService(oauth2UserService))
//                        .successHandler(successHandler)
//                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:5173", // FE web local dev
                "http://localhost:8081", // Expo dev server
                "http://10.0.243.212:8081", // React Native on physical device
                "http://172.24.16.1:8081", // React Native on emulator
                "https://your-vercel-app.vercel.app", // FE production Vercel
                "https://app.swaggerhub.com", // SwaggerHub Try it out
                "https://app.swaggerhub.com",
                "https://api-mumii.namtechie.id.vn",
                "http://localhost:8080" // Local Spring if calling from SwaggerHub
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L); // 1h

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        // IAuthenticationService now extends UserDetailsService
        provider.setUserDetailsService(authenticationService);
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
