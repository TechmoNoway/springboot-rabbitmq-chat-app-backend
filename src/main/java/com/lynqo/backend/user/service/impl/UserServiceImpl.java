package com.lynqo.backend.user.service.impl;

import com.lynqo.backend.user.dto.UpdateUserRequest;
import com.lynqo.backend.user.dto.ChangePasswordRequest;
import com.lynqo.backend.user.dto.UserFriendResponse;
import com.lynqo.backend.user.dto.UserResponse;
import com.lynqo.backend.user.dto.UserSummaryResponse;
import com.lynqo.backend.user.domain.User;
import com.lynqo.backend.user.repository.UserRepository;
import com.lynqo.backend.messaging.service.MessageService;
import com.lynqo.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final MessageService messageService;

    @Override
    public List<UserSummaryResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toSummary)
                .toList();
    }

    @Override
    public UserResponse getUserById(int id) {
        User user = userRepository.findById(id);
        return toResponse(user);
    }

    @Override
    public List<UserSummaryResponse> getUsersByUsername(String username) {
        return userRepository.findByUsername(username).stream()
                .map(this::toSummary)
                .toList();
    }

    @Override
    public boolean isUsernameExist(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public int updateUser(UpdateUserRequest updateUserDTO) {

        User user = userRepository.findById(updateUserDTO.getUserId());
        user.setUsername(updateUserDTO.getUsername());
        user.setEmail(updateUserDTO.getEmail());
        user.setAvatarUrl(updateUserDTO.getAvatarUrl());
        user.setBirthdate(updateUserDTO.getBirthdate());
        user.setPhoneNumber(updateUserDTO.getPhoneNumber());

        userRepository.save(user);


        return user.getId();
    }

    @Override
    public List<UserFriendResponse> getAllUserFriendsAndLatestMessage(int userId) {

        List<User> users = userRepository.findAllFriends(userId);

        List<UserFriendResponse> result = new ArrayList<>();

        for (User user : users) {

            UserFriendResponse response =  UserFriendResponse.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .avatarUrl(user.getAvatarUrl())
                    .isActive(user.isActive())
                    .lastMessage(messageService.getTheLatestMessage(userId, user.getId()))
                    .build();
            result.add(response);
        }


        return result;
    }

    @Override
    public List<UserSummaryResponse> searchUserByString(String str) {
        return userRepository.findAllByUsernameContaining(str).stream()
                .map(this::toSummary)
                .toList();
    }

    @Override
    public void changePassword(ChangePasswordRequest changePasswordRequest) {

    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .phoneNumber(user.getPhoneNumber())
                .birthdate(user.getBirthdate())
                .isActive(user.isActive())
                .isBlocked(user.isBlocked())
                .build();
    }

    private UserSummaryResponse toSummary(User user) {
        return UserSummaryResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .avatarUrl(user.getAvatarUrl())
                .active(user.isActive())
                .build();
    }
}
