package org.example.dataprotal.controller;

import lombok.RequiredArgsConstructor;
import org.example.dataprotal.dto.ContentDto;
import org.example.dataprotal.model.page.Content;
import org.example.dataprotal.model.page.SubContent;
import org.example.dataprotal.service.ContentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/content")
@RequiredArgsConstructor
public class ContentController {
    private final ContentService contentService;

    @GetMapping
    public ResponseEntity<Content> getContentByPageName(@RequestParam String pageName) {
        return ResponseEntity.ok(contentService.getContentByPageName(pageName));
    }

    @PostMapping
    public ResponseEntity<Content> addContent(@RequestPart("request") ContentDto contentDto, @RequestPart(required = false,name = "img") MultipartFile file) {
        final var createdContent = contentService.createContent(contentDto,file);
        final var location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/{id}").build(createdContent.getId());
        return ResponseEntity.created(location).body(createdContent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Content> updateContent(@PathVariable Long id, @RequestPart("request")ContentDto contentDto, @RequestPart(required = false,name = "img") MultipartFile file) {
        final var updatedContent = contentService.updateContent(id,contentDto,file);
        final var location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/{id}").build(updatedContent.getId());
        return ResponseEntity.created(location).body(updatedContent);
    }

}
