package com.lynqo.backend.messaging.repository;

import com.lynqo.backend.messaging.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    @Query(value = "SELECT * FROM message m WHERE (m.sender_id = :user1Id AND m.receiver_id = :user2Id) OR (m.sender_id = :user2Id AND m.receiver_id = :user1Id)", nativeQuery = true)
    List<Message> findMessagesBetweenTwoUsers(@Param("user1Id") int user1Id, @Param("user2Id") int user2Id);



}
