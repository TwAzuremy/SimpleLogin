package com.framework.simpleLogin.repository;

import com.framework.simpleLogin.entity.User;
import com.framework.simpleLogin.utils.CACHE_NAME;
import jakarta.validation.constraints.Pattern;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

@CacheConfig(cacheNames = CACHE_NAME.USER + ":cache#" + (7 * 24 * 3600 * 1000))
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(@Pattern(regexp = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Invalid email address") String email);

    @Cacheable(key = "#email")
    boolean existsUserByEmail(@Pattern(regexp = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Invalid email address") String email);
}
