package org.example.dataprotal.model.page;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Data
@Entity
@Table
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String pageName;
    String title;

    @Column(columnDefinition = "TEXT")
    String topic;
    String imgUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    List<SubContent> subContents;
}
