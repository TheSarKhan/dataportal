package org.example.dataprotal.repository.report;

import org.example.dataprotal.model.report.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic,Long> {

    List<Topic> findAllByTopic(String topic);

    List<Topic> findAllByTopicSlug(String topicSlug);
}
