package com.handwritingnote.user;

public interface EmailSender {

    void sendVerificationEmail(String to, String token);
}
