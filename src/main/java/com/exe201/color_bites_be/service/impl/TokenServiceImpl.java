package com.exe201.color_bites_be.service.impl;

import com.exe201.color_bites_be.entity.Account;
import com.exe201.color_bites_be.repository.AccountRepository;
import com.exe201.color_bites_be.service.ITokenService;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Implementation của ITokenService
 * Xử lý logic JWT token: tạo, validate, blacklist
 */
@Service
public class TokenServiceImpl implements ITokenService {
    // Danh sách lưu token bị hủy cùng thời gian hết hạn
    private Map<String, Date> blacklistedTokens = new HashMap<>();
    private final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    @Autowired
    AccountRepository accountRepository;

    public final String secretKey = dotenv.get("SECRET_KEY");

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String generateToken(Account account) {
        return Jwts.builder()
                .subject(account.getId() + "")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // Token sống trong 60 phút
                .signWith(getSignKey())
                .compact();
    }

    @Override
    public void invalidateToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        // Lưu token vào danh sách đen với thời gian hết hạn
        blacklistedTokens.put(token, claims.getExpiration());
    }

    @Override
    public Account getAccountByToken(String token) {
        cleanUpBlacklistedTokens(); // Xóa các token đã hết hạn trước khi kiểm tra

        if (blacklistedTokens.containsKey(token)) {
            throw new RuntimeException("Token này đã bị hủy.");
        }

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String idString = claims.getSubject();
            // Sử dụng findById cho MongoDB
            return accountRepository.findById(idString)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token đã hết hạn. Vui lòng đăng nhập lại.");
        } catch (Exception e) {
            throw new RuntimeException("Token không hợp lệ.");
        }
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        cleanUpBlacklistedTokens(); // Xóa các token đã hết hạn trước khi kiểm tra
        return blacklistedTokens.containsKey(token);
    }

    @Override
    public String getToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7); // Bỏ qua "Bearer "
    }

    @Override
    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    @Override
    public <T> T extractClaims(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Xóa các token đã hết hạn khỏi danh sách đen
     */
    private void cleanUpBlacklistedTokens() {
        Iterator<Map.Entry<String, Date>> iterator = blacklistedTokens.entrySet().iterator();
        Date now = new Date();

        while (iterator.hasNext()) {
            Map.Entry<String, Date> entry = iterator.next();
            // Nếu token đã hết hạn, loại bỏ nó khỏi danh sách đen
            if (entry.getValue().before(now)) {
                iterator.remove();
            }
        }
    }

    /**
     * Kiểm tra token có hết hạn không
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extract expiration date từ token
     */
    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }
}
