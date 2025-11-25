package com.ECommerce.repository;

import com.ECommerce.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByUsername(String username);


    Optional<Users> findByRefreshToken(String refreshToken);

    Optional<Users> findByEmail(String username);
}
