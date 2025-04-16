package org.example.dataprotal.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.dataprotal.dto.request.AboutRequest;
import org.example.dataprotal.dto.response.AboutResponse;
import org.example.dataprotal.model.about.About;
import org.example.dataprotal.repository.about.AboutRepository;
import org.example.dataprotal.service.AboutService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AboutServiceImpl implements AboutService {

    private final AboutRepository repository;

    private final CloudinaryServiceImpl cloudinaryService;

    @Override
    public AboutResponse getAbout(Long id) {

        About about = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("about not found"));

        if (!"ACTIVE".equals(about.getStatus())) {
            throw new IllegalStateException("About section is not active");
        }

        return AboutResponse.builder()
                .id(about.getId())
                .title(about.getTitle())
                .description(about.getDescription())
                .imageUrl(about.getImageUrl())
                .build();

    }

    @Override
    public List<AboutResponse> getAllAbouts() {
        List<About> abouts = repository.findAll();

        return abouts.stream().filter(a -> a.getStatus().equals("ACTIVE")).map(a -> AboutResponse.builder()
                .id(a.getId())
                .title(a.getTitle())
                .description(a.getDescription())
                .imageUrl(a.getImageUrl())
                .build()).collect(Collectors.toList());
    }

    @Override
    public void addAbout(AboutRequest about) throws IOException {

        String imageUrl = cloudinaryService.uploadFile(about.getImage(), "about-image");

        About about1 = About.builder()
                .title(about.getTitle())
                .description(about.getDescription())
                .imageUrl(imageUrl)
                .build();

        repository.save(about1);

    }

    @Override
    public void updateAbout(AboutRequest about, Long id) throws IOException {

        About about1=repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("about not found"));

        if (!"ACTIVE".equals(about1.getStatus())) {
            throw new IllegalStateException("About section is not active");
        }else{
            String imageUrl=cloudinaryService.uploadFile(about.getImage(), "update-about");
            about1.setTitle(about.getTitle());
            about1.setDescription(about.getDescription());
            about1.setImageUrl(imageUrl);

            repository.save(about1);
        }

    }

    @Override
    public void deleteAbout(Long id) {

        About about=repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("about not found"));

        if(!"ACTIVE".equals(about.getStatus())) {
            throw new IllegalStateException("About section is not active");
        }else{
            about.setStatus("DELETED");
            repository.save(about);
        }

    }
}
