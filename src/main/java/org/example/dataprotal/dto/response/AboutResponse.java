package org.example.dataprotal.dto.response;


import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AboutResponse {

    private Long id;

    private String title;

    private String description;

    private String imageUrl;

}
