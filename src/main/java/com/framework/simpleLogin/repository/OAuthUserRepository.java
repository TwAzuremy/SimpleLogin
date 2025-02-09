package com.framework.simpleLogin.repository;

import com.framework.simpleLogin.entity.OAuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OAuthUserRepository extends JpaRepository<OAuthUser, Long> {
    @Query("select o from OAuthUser o where o.provider = :provider and o.providerId = :providerId")
    Optional<OAuthUser> findByProviderAndProviderId(@Param("provider") String provider, @Param("providerId") String providerId);
}
