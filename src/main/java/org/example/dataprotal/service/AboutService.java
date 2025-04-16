package org.example.dataprotal.service;

import org.example.dataprotal.dto.request.AboutRequest;
import org.example.dataprotal.dto.response.AboutResponse;

import java.io.IOException;
import java.util.List;

public interface AboutService {

    AboutResponse getAbout(Long id);

    List<AboutResponse> getAllAbouts();

    void addAbout(AboutRequest about) throws IOException;

    void updateAbout(AboutRequest about, Long id) throws IOException;

    void deleteAbout(Long id);

}
