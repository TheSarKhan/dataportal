package org.example.dataprotal.model.report;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;
    String topic;
    String topicIcon;
    String topicSlug;
    public void generateTopicSlug() {
        if (topic != null && !topic.isBlank()) {
            this.topicSlug = topic.toLowerCase()
                    .replaceAll("[^a-z0-9\\s]", "") // özel karakterleri sil
                    .replaceAll("\\s+", "-");       // boşlukları - ile değiştir
        }
    }
}
