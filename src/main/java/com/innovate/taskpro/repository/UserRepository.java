package com.innovate.taskpro.repository;

import com.innovate.taskpro.entity.UserBO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserBO, String> {

    Optional<UserBO> findByUserName(String userName);

    boolean existsByUserName(String userName);
}
