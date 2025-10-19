package com.exe201.color_bites_be.service;

public interface IEmailService {
    void sendOtpEmail(String to, String otp);
    void sendForgotPasswordEmail(String to, String otp);

}
