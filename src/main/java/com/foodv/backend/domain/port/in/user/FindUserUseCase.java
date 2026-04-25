package com.foodv.backend.domain.port.in.user;

import com.foodv.backend.domain.model.user.User;

import java.util.List;

public interface FindUserUseCase {

    User findById(Long id);

    User findByEmail(String email);

    List<User> findAll();
}
