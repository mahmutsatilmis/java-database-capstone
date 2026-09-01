package com.project.back_end.services;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class TokenService {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Value("${jwt.secret}")
    private String secret;

    public TokenService(AdminRepository adminRepository,
                        DoctorRepository doctorRepository,
                        PatientRepository patientRepository) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    public String generateToken(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            return null;
        }

        return Jwts.builder()
                .subject(identifier.trim())
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plus(7, ChronoUnit.DAYS)))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    public String extractIdentifier(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }

        String normalizedToken = token.trim();
        if (normalizedToken.startsWith("Bearer ") || normalizedToken.startsWith("bearer ")) {
            normalizedToken = normalizedToken.substring(7).trim();
        }

        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(normalizedToken)
                    .getPayload()
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public String extractEmail(String token) {
        return extractIdentifier(token);
    }

    public boolean validateToken(String token, String user) {
        if (token == null || token.isBlank() || user == null || user.isBlank()) {
            return false;
        }

        String identifier = extractIdentifier(token);
        if (identifier == null || identifier.isBlank()) {
            return false;
        }

        String normalizedUser = user.trim().toLowerCase(Locale.ROOT);
        String lookupValue = identifier.trim();

        if (normalizedUser.equals("admin")) {
            return adminRepository.findByUsername(lookupValue) != null;
        }

        if (normalizedUser.equals("doctor")) {
            return doctorRepository.findByEmail(lookupValue) != null;
        }

        if (normalizedUser.equals("patient")) {
            return patientRepository.findByEmail(lookupValue) != null;
        }

        return false;
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            keyBytes = Arrays.copyOf(keyBytes, 32);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
