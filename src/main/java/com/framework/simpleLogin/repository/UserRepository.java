package com.framework.simpleLogin.repository;

import com.framework.simpleLogin.dto.UserResponse;
import com.framework.simpleLogin.entity.User;
import com.framework.simpleLogin.utils.CONSTANT;
import jakarta.validation.constraints.Pattern;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findUserByEmail(@Pattern(regexp = CONSTANT.REGEX.EMAIL, message = "Invalid email address") String email);

    User findUserById(int id);

    @Cacheable(cacheNames = CONSTANT.CACHE_NAME.USER_CACHE + ":exists@user-cache" , key = "#email")
    boolean existsUserByEmail(@Pattern(regexp = CONSTANT.REGEX.EMAIL, message = "Invalid email address") String email);

    @Cacheable(cacheNames = CONSTANT.CACHE_NAME.USER_CACHE + ":info@user-cache" ,key = "#id")
    @Query("select u from User u where u.id = :id")
    UserResponse findUserExcludePasswordById(int id);

    @Transactional
    @Modifying
    @Query("update User u set u.password = :password where u.id = :id")
    int updatePasswordById(String password, int id);
}
