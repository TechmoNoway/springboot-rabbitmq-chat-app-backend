package com.lynqo.backend.friendship.service.impl;

import com.lynqo.backend.friendship.domain.FriendshipStatus;
import com.lynqo.backend.friendship.domain.Friendship;
import com.lynqo.backend.friendship.repository.FriendshipRepository;
import com.lynqo.backend.user.repository.UserRepository;
import com.lynqo.backend.friendship.service.FriendshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendshipServiceImpl implements FriendshipService {

    private final FriendshipRepository friendRepository;

    private final UserRepository userRepository;

    @Override
    public boolean checkIsFriend(int userId, int friendId) {
        List<Friendship> friends = friendRepository.findByUserIdAndFriendId(userId, friendId);
        return !friends.isEmpty();
    }

    @Override
    public void addFriend(int userId, int friendId, String status) {
        Friendship friendship = Friendship.builder()
                .user(userRepository.findById(userId))
                .friend(userRepository.findById(friendId))
                .status(FriendshipStatus.valueOf("accepted"))
                .build();
        friendRepository.save(friendship);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        friendRepository.removeByUserIdAndFriendId(userId, friendId);
    }
}
