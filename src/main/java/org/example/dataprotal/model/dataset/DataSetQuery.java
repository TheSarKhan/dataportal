package org.example.dataprotal.model.dataset;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DataSetQuery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String fullName;
    String email;
    String phoneNumber;
    String organization;
    String dataSetName;
    String purpose;
    @Column(columnDefinition = "TEXT")
    String notes;
    Boolean isRobot;
}
