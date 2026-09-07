package com.lynqo.backend.friendship.service;

public interface FriendshipService {

    boolean checkIsFriend(int userId, int friendId);

    void addFriend(int userId, int friendId, String status);

    void removeFriend(int userId, int friendId);

}
