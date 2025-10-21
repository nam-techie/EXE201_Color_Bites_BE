package com.exe201.color_bites_be.config;

import com.exe201.color_bites_be.entity.Account;
import com.exe201.color_bites_be.service.ITokenService;
import jakarta.security.auth.message.AuthException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    @Lazy  // ✅ Thêm @Lazy để tránh circular dependency khi Filter khởi tạo sớm
    private ITokenService tokenService;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    HandlerExceptionResolver handlerExceptionResolver;

    private final List<String> AUTH_PERMISSION = List.of(
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/forgot-password",
            "/api/loginByGoogle",
            "/oauth2/authorization/**",
            "/login/oauth2/code/**",
            "/api/vnpay-return",
            "/api/restaurants/nearby",
            "/api/restaurants/in-bounds",
            "/api/restaurants/by-district",
            "/api/restaurants/read/by-district/**",
            "/api/restaurants/search",
            "/api/restaurants/reverse-geocode",
            "/api/otp/verify-register",
            "/api/otp/verify-reset-password"
    );

    public boolean checkIsPublicAPI(String uri) {
        AntPathMatcher pathMatch = new AntPathMatcher();
        return AUTH_PERMISSION.stream().anyMatch(pattern -> pathMatch.match(pattern, uri));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        boolean isPublicAPI = checkIsPublicAPI(request.getRequestURI());
        if (isPublicAPI) {
            filterChain.doFilter(request, response);
        } else {
            String authHeader = request.getHeader("Authorization");
            String token = tokenService.getToken(authHeader);
            if (token == null) {
                handlerExceptionResolver.resolveException(request, response, null, new AuthException("Empty token!"));
                return;
            }

            try {
                // Kiểm tra token trong danh sách đen
                if (tokenService.isTokenBlacklisted(token)) {
                    handlerExceptionResolver.resolveException(request, response, null, new AuthException("Token này đã bị hủy!"));
                    return;
                }

                Account account = tokenService.getAccountByToken(token);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        account, token, account.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                handlerExceptionResolver.resolveException(request, response, null, new AuthException(e.getMessage()));
            }
        }
    }
}
