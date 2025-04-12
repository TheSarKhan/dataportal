package org.example.dataprotal.repository.report;

import org.example.dataprotal.model.report.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card,Long> {

    List<Card> findByTopicSlugAndSubTopicSlug(String topicSlug, String subTopicSlug);
}
