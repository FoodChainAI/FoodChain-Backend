package com.example.foodchain.users.mapper;

import com.example.foodchain.users.dto.UserResponse;
import com.example.foodchain.users.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.isVerified(),
                user.getCreatedAt());
    }
}
