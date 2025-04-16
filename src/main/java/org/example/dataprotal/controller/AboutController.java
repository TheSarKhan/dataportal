package org.example.dataprotal.controller;

import lombok.RequiredArgsConstructor;
import org.example.dataprotal.dto.request.AboutRequest;
import org.example.dataprotal.dto.response.AboutResponse;
import org.example.dataprotal.service.AboutService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/about")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AboutController {

    private final AboutService service;

    @GetMapping("get-id/{id}")
    public AboutResponse getAbout(@PathVariable Long id) {
        return service.getAbout(id);
    }

    @GetMapping("get-all")
    public List<AboutResponse> getAllAbouts() {
        return service.getAllAbouts();
    }

    @PostMapping
    public void addAbout(@RequestBody AboutRequest about) throws IOException {
        service.addAbout(about);
    }

    @PutMapping("update-id/{id}")
    public void updateAbout(@RequestBody AboutRequest about,
                            @PathVariable Long id) throws IOException {
        service.updateAbout(about, id);
    }

    @DeleteMapping("delete-by-id/{id}")
    public void deleteAbout(@PathVariable Long id) {
        service.deleteAbout(id);
    }

}
