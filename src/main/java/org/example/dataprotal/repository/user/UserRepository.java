package org.example.dataprotal.repository.user;

import org.example.dataprotal.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query(value = "select * from users where similarity(CONCAT(firstName, ' ', lastName), :name) > 0.3", nativeQuery = true)
    List<User> searchUserByName(String name);
}