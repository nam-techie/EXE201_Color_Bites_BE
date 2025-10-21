package com.exe201.color_bites_be.service.impl;

import com.exe201.color_bites_be.service.IEmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements IEmailService {

    @Autowired
    JavaMailSender mailSender;

    @Override
    public void sendOtpEmail(String to, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Mã Xác Thực OTP - Ứng Dụng Mumii");

            String htmlContent = buildOtpTemplate(otp);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new IllegalStateException("Không thể gửi email OTP", e);
        }
    }

    public void sendForgotPasswordEmail(String to, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Đặt Lại Mật Khẩu - Mã Xác Nhận Từ Mumii");
            helper.setText(buildForgotPasswordTemplate(otp), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new IllegalStateException("Không thể gửi email quên mật khẩu", e);
        }
    }

    private String buildOtpTemplate(String otp) {
        return """
    <html>
      <body style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #fffaf3; padding: 30px;">
        <div style="max-width: 600px; margin: auto; background: #ffffff; border-radius: 18px; padding: 40px;
                    box-shadow: 0 6px 20px rgba(0,0,0,0.1); border-top: 6px solid #ff9800;">
          <div style="text-align: center;">
            <h2 style="color: #ff9800; font-size: 26px; margin-bottom: 10px;">🍽️ Xác Thực Tài Khoản Mumii</h2>
            <p style="color: #555; font-size: 15px; margin-top: 0;">Xin chào bạn thân mến,</p>
          </div>
          <p style="font-size: 16px; color: #333; margin-top: 25px;">
            Cảm ơn bạn đã tin tưởng và sử dụng <b>Mumii</b>!
            Để hoàn tất bước xác thực, vui lòng nhập mã OTP dưới đây:
          </p>
          <div style="text-align: center; margin: 25px 0;">
            <span style="font-size: 34px; letter-spacing: 6px; font-weight: bold; color: #ff5722;
                         background: #fff3e0; padding: 12px 24px; border-radius: 10px; display: inline-block;">
              %s
            </span>
          </div>
          <p style="font-size: 15px; color: #555; margin-top: 10px;">
            ⏰ Mã OTP này có hiệu lực trong vòng <b>5 phút</b>.
            Vui lòng không chia sẻ mã với bất kỳ ai để đảm bảo an toàn cho tài khoản của bạn.
          </p>
          <hr style="margin: 30px 0; border: none; border-top: 1px solid #eee;"/>
          <p style="font-size: 13px; color: #777; text-align: center;">
            Nếu bạn không yêu cầu mã này, vui lòng bỏ qua email này.
            <br/>Đội ngũ hỗ trợ <b>Mumii</b> luôn sẵn sàng giúp đỡ bạn khi cần thiết.
          </p>
          <p style="font-size: 12px; color: #999; text-align: center; margin-top: 25px;">
            © 2025 <b>Mumii</b> — Cùng bạn tận hưởng từng bữa ăn ngon ❤️
          </p>
        </div>
      </body>
    </html>
    """.formatted(otp);
    }

    private String buildForgotPasswordTemplate(String otp) {
        return """
    <html>
      <body style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f6f9fc; padding: 30px;">
        <div style="max-width: 600px; margin: auto; background: #ffffff; border-radius: 18px; padding: 40px;
                    box-shadow: 0 6px 20px rgba(0,0,0,0.1); border-top: 6px solid #2196f3;">
          <div style="text-align: center;">
            <h2 style="color: #2196f3; font-size: 26px; margin-bottom: 10px;">🔐 Đặt Lại Mật Khẩu Mumii</h2>
            <p style="color: #555; font-size: 15px; margin-top: 0;">Xin chào,</p>
          </div>
          <p style="font-size: 16px; color: #333; margin-top: 25px;">
            Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản <b>Mumii</b> của bạn.
            Hãy sử dụng mã OTP bên dưới để tiếp tục quá trình:
          </p>
          <div style="text-align: center; margin: 25px 0;">
            <span style="font-size: 34px; letter-spacing: 6px; font-weight: bold; color: #1976d2;
                         background: #e3f2fd; padding: 12px 24px; border-radius: 10px; display: inline-block;">
              %s
            </span>
          </div>
          <p style="font-size: 15px; color: #555; margin-top: 10px;">
            ⏰ Mã OTP này chỉ có hiệu lực trong <b>5 phút</b>.
            Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email.
          </p>
          <hr style="margin: 30px 0; border: none; border-top: 1px solid #eee;"/>
          <p style="font-size: 13px; color: #777; text-align: center;">
            Đội ngũ <b>Mumii</b> luôn sẵn sàng hỗ trợ bạn khi cần thiết.
            <br/>Giữ tài khoản của bạn an toàn là ưu tiên hàng đầu của chúng tôi ❤️
          </p>
          <p style="font-size: 12px; color: #999; text-align: center; margin-top: 25px;">
            © 2025 <b>Mumii</b> — Cùng bạn tận hưởng từng bữa ăn ngon ❤️
          </p>
        </div>
      </body>
    </html>
    """.formatted(otp);
    }
}
