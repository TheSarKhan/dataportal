//package org.example.dataprotal.model.report;
//
//import jakarta.persistence.*;
//import lombok.AccessLevel;
//import lombok.Data;
//import lombok.experimental.FieldDefaults;
//
//@Data
//@Entity
//@FieldDefaults(level = AccessLevel.PRIVATE)
//public class SubTopic {
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
//    Long id;
//    String subTopic;
//    String topic;
//    String topicSlug;
//    String subTopicSlug;
//
//    public void generateSubTopicSlug() {
//        if (subTopic != null && !subTopic.isBlank()) {
//            this.subTopicSlug = subTopic.toLowerCase()
//                    .replaceAll("[^a-z0-9\\s]", "") // özel karakterleri sil
//                    .replaceAll("\\s+", "-");       // boşlukları - ile değiştir
//        }
//    }
//
//
//    public void generateTopicSlug() {
//        if (topic != null && !topic.isBlank()) {
//            this.topicSlug = topic.toLowerCase()
//                    .replaceAll("[^a-z0-9\\s]", "") // özel karakterleri sil
//                    .replaceAll("\\s+", "-");       // boşlukları - ile değiştir
//        }
//    }
//}
