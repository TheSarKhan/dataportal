package org.example.dataprotal.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AboutRequest {

    @NotBlank(message = "tittle must not be empty")
    private String title;

    @Column(nullable = false)
    private String description;

    private MultipartFile image;

}
