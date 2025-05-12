package org.example.dataprotal.model.dataset;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DataSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String author;
    String dataSetName;
    String title;

    @Column(columnDefinition = "TEXT")
    String description;
    String imageUrl;
    String fileUrl;

    @CreationTimestamp
    @DateTimeFormat(pattern = "yyyy.MM.dd")
    LocalDateTime createdAt;
}
