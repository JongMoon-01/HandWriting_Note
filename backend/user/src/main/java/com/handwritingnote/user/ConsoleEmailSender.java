package com.handwritingnote.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 실제 이메일 발송(SMTP/SES 등) 연동 전까지 사용하는 임시 구현.
 * 로그로 토큰을 출력한다 — 운영 배포 전 반드시 실제 구현으로 교체할 것 (TODO).
 */
@Component
public class ConsoleEmailSender implements EmailSender {

    private static final Logger log = LoggerFactory.getLogger(ConsoleEmailSender.class);

    @Override
    public void sendVerificationEmail(String to, String token) {
        log.info("[TODO: 실제 이메일 발송으로 교체] to={} verification token={}", to, token);
    }
}
