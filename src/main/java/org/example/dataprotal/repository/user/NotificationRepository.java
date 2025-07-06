package org.example.dataprotal.repository.user;

import org.example.dataprotal.model.user.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> getByReceiverId(Long receiverId);

    List<Notification> getBySenderId(Long senderId);

    @Query("from Notification where receiverId = :receiverId and isSeen = false")
    List<Notification> getByReceiverIdAndNotSeen(Long receiverId);

    @Query(value = "select * from notificatyions where title % :title", nativeQuery = true)
    List<Notification> searchNotificationByName(String title);
}
