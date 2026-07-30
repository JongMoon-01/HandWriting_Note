package com.handwritingnote.user;

import java.util.Map;

/**
 * Google/Naver/Kakao의 OAuth2 사용자 정보 응답 구조가 서로 달라서
 * (Naver는 response로 감싸져 있고, Kakao는 kakao_account.profile 안에 nickname이 있음)
 * provider별 attribute 파싱을 여기 한 곳에 모아둔다.
 * 실제 응답 구조는 각 provider 문서로 재확인 필요 — 아직 실제 호출로 검증되지 않음 (TODO).
 */
record SocialProfile(String providerId, String email, String name) {

    @SuppressWarnings("unchecked")
    static SocialProfile from(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) {
            case "google" -> new SocialProfile(
                    String.valueOf(attributes.get("sub")),
                    String.valueOf(attributes.get("email")),
                    String.valueOf(attributes.get("name")));
            case "naver" -> {
                Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                yield new SocialProfile(
                        String.valueOf(response.get("id")),
                        String.valueOf(response.get("email")),
                        String.valueOf(response.get("name")));
            }
            case "kakao" -> {
                Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
                Map<String, Object> profile = (Map<String, Object>) account.get("profile");
                yield new SocialProfile(
                        String.valueOf(attributes.get("id")),
                        String.valueOf(account.get("email")),
                        String.valueOf(profile.get("nickname")));
            }
            default -> throw new IllegalArgumentException("지원하지 않는 provider: " + registrationId);
        };
    }
}
