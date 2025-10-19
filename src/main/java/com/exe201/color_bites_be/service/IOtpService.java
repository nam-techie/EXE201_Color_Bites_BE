package com.exe201.color_bites_be.service;

public interface IOtpService {
    String generateOtp(String email);
    boolean verifyOtp(String email, String otp);
}
