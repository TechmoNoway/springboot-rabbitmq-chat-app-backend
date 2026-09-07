package com.lynqo.backend.friendship.repository;

import com.lynqo.backend.friendship.domain.Friendship;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, Integer> {
    @Query(value = "SELECT * FROM friendship WHERE (user_id = :userId AND friend_id = :friendId) OR (user_id = :friendId AND friend_id = :userId) AND (status = 'accepted')", nativeQuery = true)
    List<Friendship> findByUserIdAndFriendId(@Param("userId") int userId, @Param("friendId") int friendId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM friendship WHERE (user_id = :userId AND friend_id = :friendId) OR (user_id = :friendId AND friend_id = :userId)", nativeQuery = true)
    void removeByUserIdAndFriendId(@Param("userId") int userId, @Param("friendId") int friendId);
}
