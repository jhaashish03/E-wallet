package com.ewallet.notificationservice.services;

import com.ewallet.notificationservice.entities.EmailRequest;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_RELATED;

@Service
@Slf4j
public class MailService {

    private final JavaMailSender javaMailSender;
    private final Environment environment;

    @Autowired
    public MailService(JavaMailSender javaMailSender, Environment environment) {
        this.javaMailSender = javaMailSender;
        this.environment= environment;
    }

    public void triggerMail(EmailRequest emailRequest){

        MimeMessagePreparator mimeMessagePreparator=new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper mimeMessageHelper=new MimeMessageHelper(mimeMessage, MULTIPART_MODE_RELATED, "UTF-8");
                mimeMessageHelper.setFrom(Objects.requireNonNull(environment.getProperty("java-mail-sender-from-email-address")));
                mimeMessageHelper.setTo(emailRequest.getTo());
                mimeMessageHelper.setText(emailRequest.getBody());
                mimeMessageHelper.setSubject(emailRequest.getSubject());

            }
        };
        javaMailSender.send(mimeMessagePreparator);

    }
}
