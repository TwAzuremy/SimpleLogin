package com.framework.simpleLogin.repository;

import com.framework.simpleLogin.entity.User;
import com.framework.simpleLogin.utils.CONSTANT;
import jakarta.validation.constraints.Pattern;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

@CacheConfig(cacheNames = CONSTANT.CACHE_NAME.USER_CACHE + "#" + CONSTANT.CACHE_EXPIRATION_TIME.USER_CACHE)
public interface UserRepository extends JpaRepository<User, Integer> {
    User findUserByEmail(@Pattern(regexp = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Invalid email address") String email);

    @Cacheable(key = "#email")
    boolean existsUserByEmail(@Pattern(regexp = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Invalid email address") String email);
}
