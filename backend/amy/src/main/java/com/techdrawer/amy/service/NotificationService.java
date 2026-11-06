package com.techdrawer.amy.service;

import com.techdrawer.amy.model.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender javaMailSender;

    public void sendValidation(Validation validation) {

        SimpleMailMessage message = new SimpleMailMessage();

        String text = String.format(
                "Hello %s \nThis is your activation code %s \nSee you on the activation page",
                validation.getUser().getName(),
                validation.getCode()
        );

        message.setSubject("Validation Code");
        message.setFrom("no-reply@amy.art");
        message.setTo(validation.getUser().getEmail());
        message.setText(text);

        javaMailSender.send(message);
    }

    public void sendResetPasswordValidation(Validation validation) {

        SimpleMailMessage message = new SimpleMailMessage();

        String text = String.format(
                "Hello %s \nThis is your reset password validation code %s \nSee you on the reset password page",
                validation.getUser().getName(),
                validation.getCode()
        );

        message.setSubject("Reset Password Code");
        message.setFrom("no-reply@amy.art");
        message.setTo(validation.getUser().getEmail());
        message.setText(text);

        javaMailSender.send(message);
    }
}
