package com.framework.simpleLogin.repository;

import com.framework.simpleLogin.entity.User;
import com.framework.simpleLogin.utils.CONSTANT;
import jakarta.validation.constraints.Pattern;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

@CacheConfig(cacheNames = CONSTANT.CACHE_NAME.USER_CACHE + "@user-cache")
public interface UserRepository extends JpaRepository<User, Integer> {
    User findUserByEmail(@Pattern(regexp = CONSTANT.REGEX.EMAIL, message = "Invalid email address") String email);

    @Cacheable(key = "#email")
    boolean existsUserByEmail(@Pattern(regexp = CONSTANT.REGEX.EMAIL, message = "Invalid email address") String email);
}
