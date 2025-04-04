package org.example.dataprotal.email.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConsultationEmail {
    String email;
    String nameSurname;
    String phone;
    String area;
}