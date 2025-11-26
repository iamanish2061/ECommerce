package com.ECommerce.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public boolean sendEmail(EmailSender emailSender) {
        try{
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(emailSender.to);
            mail.setSubject(emailSender.subject);
            mail.setText(emailSender.body);
            javaMailSender.send(mail);
            return true;
        }catch (Exception e){
            log.error("Exception while sending mail! :{}", e.getMessage());
            return false;
        }

    }

    public record EmailSender(String to, String subject, String body){}

}
