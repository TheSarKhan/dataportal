package org.example.dataprotal.model.company;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;
    String companyName;
    String companyRegisterNumber;
    int createYear;
    String address;
    String cityAndRegion;
    String website;
    String contactName;
    String contactEmail;
    String contactPhone;
}
