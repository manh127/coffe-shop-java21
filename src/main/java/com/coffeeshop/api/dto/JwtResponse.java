package com.coffeeshop.api.dto;

public record JwtResponse(String accessToken, String tokenType, long expiresIn) {
    public static JwtResponse of(String token, long expiresInMs) {
        return new JwtResponse(token, "Bearer", expiresInMs / 1000);
    }
}



