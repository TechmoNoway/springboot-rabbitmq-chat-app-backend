package com.lynqo.backend.user.repository;

import com.lynqo.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findAll();

    User findById(int id);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query(value = "SELECT * FROM user WHERE id IN " +
            "(SELECT friend_id FROM friendship WHERE user_id = :userId AND status = 'accepted' " +
            "UNION SELECT user_id FROM friendship WHERE friend_id = :userId AND status = 'accepted')",
            nativeQuery = true)
    List<User> findAllFriends(@Param("userId") int userId);

    List<User> findAllByUsernameContaining(String keyword);


}
