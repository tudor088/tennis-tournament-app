package com.example.tournament.service;

import com.example.tournament.entity.Tournament;
import com.example.tournament.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendRegistrationDecision(User to, Tournament t, boolean accepted) {
        String subject = "Tournament registration " + (accepted ? "approved" : "denied");
        String text = String.format("""
                Hi %s,
                
                Your request to join the tournament "%s" has been %s.
                
                Regards,
                Tournament admin
                """, to.getUsername(), t.getName(), accepted ? "APPROVED ✅" : "DENIED ❌");

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to.getEmail());
        msg.setSubject(subject);
        msg.setText(text);
        mailSender.send(msg);
    }
}
