package com.lynqo.backend.user.service;

import com.lynqo.backend.user.dto.UpdateUserRequest;
import com.lynqo.backend.user.dto.ChangePasswordRequest;
import com.lynqo.backend.user.dto.UserFriendResponse;
import com.lynqo.backend.user.dto.UserResponse;
import com.lynqo.backend.user.dto.UserSummaryResponse;

import java.util.List;

public interface UserService {

    List<UserSummaryResponse> getAllUsers();

    UserResponse getUserById(int id);

    List<UserSummaryResponse> getUsersByUsername(String username);

    boolean isUsernameExist(String username);

    int updateUser(UpdateUserRequest updateUserDTO);

    List<UserFriendResponse> getAllUserFriendsAndLatestMessage(int userId);

    List<UserSummaryResponse> searchUserByString(String str);

    void changePassword(ChangePasswordRequest changePasswordRequest);
}
