package com.lynqo.backend.user.service;

import com.lynqo.backend.user.dto.UpdateUserRequest;
import com.lynqo.backend.user.dto.ChangePasswordRequest;
import com.lynqo.backend.user.dto.UserFriendResponse;
import com.lynqo.backend.user.dto.UserResponse;
import com.lynqo.backend.user.domain.User;

import java.util.List;

public interface UserService {

    List<UserResponse> getAllUsers();

    User getUserById(int id);

    List<UserResponse> getUsersByUsername(String username);

    boolean isUsernameExist(String username);

    int updateUser(UpdateUserRequest updateUserDTO);

    List<UserFriendResponse> getAllUserFriendsAndLatestMessage(int userId);

    List<UserResponse> searchUserByString(String str);

    void changePassword(ChangePasswordRequest changePasswordRequest);
}
