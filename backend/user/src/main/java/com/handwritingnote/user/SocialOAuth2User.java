package com.handwritingnote.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

/**
 * Spring Security의 OAuth2User에 우리 내부 User 엔티티를 얹어서 함께 들고 다니기 위한 래퍼.
 * OAuth2LoginSuccessHandler에서 principal을 이 타입으로 캐스팅해 User를 꺼내 JWT를 발급한다.
 */
public class SocialOAuth2User implements OAuth2User {

    private final OAuth2User delegate;
    private final User user;

    public SocialOAuth2User(OAuth2User delegate, User user) {
        this.delegate = delegate;
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return delegate.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return delegate.getAuthorities();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }
}
