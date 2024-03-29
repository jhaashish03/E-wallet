package com.ewallet.notificationservice.configurations.mailConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailSenderConfiguration {


    private final Environment environment;

    @Autowired
    public MailSenderConfiguration(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public JavaMailSender javaMailSender(){
        JavaMailSenderImpl javaMailSender=new JavaMailSenderImpl();
        javaMailSender.setHost("smtp.googlemail.com");
        javaMailSender.setUsername(environment.getProperty("java-mail-sender-user-name"));
        javaMailSender.setPassword(environment.getProperty("java-mail-sender-user-password"));
        javaMailSender.setPort(587);
        javaMailSender.setDefaultEncoding("utf-8");
        Properties javaMailProperties = javaMailSender.getJavaMailProperties();
        javaMailProperties.put("mail.smtp.starttls.enable",true);
        javaMailProperties.put("mail.debug",true);
        javaMailProperties.setProperty("mail.mime.charset", "utf-8");
        javaMailProperties.setProperty("mail.smtp.allow8bitmime", "true");
        javaMailProperties.setProperty("mail.smtps.allow8bitmime", "true");
//        javaMailProperties.put("mail.smtp.host","192.168.1.35");
        return javaMailSender;
    }
}
