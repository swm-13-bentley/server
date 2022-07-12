package com.schedch.mvp.repository;

import com.schedch.mvp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
