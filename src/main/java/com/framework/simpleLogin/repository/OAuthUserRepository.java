package com.framework.simpleLogin.repository;

import com.framework.simpleLogin.entity.OAuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface OAuthUserRepository extends JpaRepository<OAuthUser, String> {
    @Query("select o from OAuthUser o where o.provider = :provider and o.providerId = :providerId")
    Optional<OAuthUser> findByProviderAndProviderId(@Param("provider") String provider, @Param("providerId") String providerId);

    @Transactional
    @Modifying
    @Query("update OAuthUser o set o.user.id = :userId where o.id = :id")
    int updateUserIdById(Long userId, String id);

    @Transactional
    @Modifying
    @Query("delete from OAuthUser o where o.user.id = :userId and o.id = :id")
    int deleteOAuthUserByUserIdAndId(Long userId, String id);
}
