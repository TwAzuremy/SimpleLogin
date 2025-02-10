package com.framework.simpleLogin.repository;

import com.framework.simpleLogin.dto.UserResponse;
import com.framework.simpleLogin.entity.User;
import com.framework.simpleLogin.utils.CONSTANT;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u where u.username = :string or u.email = :string")
    Optional<User> findUserByUsernameOrEmail(String string);

    Optional<User> findUserById(Long id);

    @Cacheable(cacheNames = CONSTANT.CACHE_NAME.USER_CACHE + ":exists@user-cache" , key = "#email")
    @Query("select exists (select 1 from User u where u.email = :email)")
    boolean existsUserByEmail(String email);

    @Cacheable(cacheNames = CONSTANT.CACHE_NAME.USER_CACHE + ":info@user-cache" ,key = "#id")
    @Query("select u from User u where u.id = :id")
    Optional<UserResponse> findUserExcludePasswordById(long id);

    @Transactional
    @Modifying
    @Query("update User u set u.password = :password where u.id = :id")
    long updatePasswordById(String password, long id);
}
