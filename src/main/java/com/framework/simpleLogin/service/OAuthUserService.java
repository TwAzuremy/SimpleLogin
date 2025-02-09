package com.framework.simpleLogin.service;

import com.framework.simpleLogin.domain.OAuthUserDetails;
import com.framework.simpleLogin.domain.OAuthUserInfo;
import com.framework.simpleLogin.entity.OAuthUser;
import com.framework.simpleLogin.entity.User;
import com.framework.simpleLogin.factory.OAuthUserInfoFactory;
import com.framework.simpleLogin.repository.OAuthUserRepository;
import com.framework.simpleLogin.repository.UserRepository;
import jakarta.annotation.Resource;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class OAuthUserService extends DefaultOAuth2UserService {
    @Resource
    private OAuthUserRepository oAuthUserRepository;

    @Resource
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(request);
        String registrationId = request.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuthUserInfo userInfo = OAuthUserInfoFactory.getUserInfo(registrationId, attributes);
        User user = findOrCreateUser(userInfo, registrationId);

        return new OAuthUserDetails(user, attributes);
    }

    public User findOrCreateUser(OAuthUserInfo userInfo, String provider) {
        Optional<OAuthUser> oAuthUser = oAuthUserRepository.findByProviderAndProviderId(provider, userInfo.getProviderId());

        if (oAuthUser.isPresent()) {
            return oAuthUser.get().getUser();
        } else {
            User newUser = new User();
            newUser.setUsername(userInfo.getUsername());
            newUser.setEmail(userInfo.getEmail());
            userRepository.save(newUser);

            OAuthUser newOAuthUser = new OAuthUser();
            newOAuthUser.setUser(newUser);
            newOAuthUser.setProvider(provider);
            newOAuthUser.setProviderId(userInfo.getProviderId());
            oAuthUserRepository.save(newOAuthUser);

            return newUser;
        }
    }
}
