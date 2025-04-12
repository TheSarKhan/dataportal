package org.example.dataprotal.model.report;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;
    String topic;
    String subTopic;
    String title;
    String description;
    String topicSlug;
    String subTopicSlug;
    @JdbcTypeCode(SqlTypes.JSON)
    List<String> file;

    public void generateSubTopicSlug() {
        if (subTopic != null && !subTopic.isBlank()) {
            this.subTopicSlug = subTopic.toLowerCase()
                    .replaceAll("[^a-z0-9\\s]", "") // özel karakterleri sil
                    .replaceAll("\\s+", "-");       // boşlukları - ile değiştir
        }
    }

    public void generateTopicSlug() {
        if (topic != null && !topic.isBlank()) {
            this.topicSlug = topic.toLowerCase()
                    .replaceAll("[^a-z0-9\\s]", "") // özel karakterleri sil
                    .replaceAll("\\s+", "-");       // boşlukları - ile değiştir
        }
    }
}
