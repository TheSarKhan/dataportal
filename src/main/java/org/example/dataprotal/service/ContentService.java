package org.example.dataprotal.service;

import lombok.RequiredArgsConstructor;
import org.example.dataprotal.dto.ContentDto;
import org.example.dataprotal.mapper.ContentMapper;
import org.example.dataprotal.model.page.Content;
import org.example.dataprotal.model.page.SubContent;
import org.example.dataprotal.repository.page.ContentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ContentService {
    private final ContentRepository contentRepository;
    private final ContentMapper contentMapper;

    public Content createContent(ContentDto contentDto, MultipartFile file) {
        Content content= contentMapper.dtoToEntity(contentDto);
        return contentRepository.save(content);
    }


    public Content getContentByPageName(String pageName) {
        return contentRepository.findByPageName(pageName).orElseThrow(()-> new RuntimeException("Content not found"));
    }

    public Content updateContent(Long id, ContentDto contentDto, MultipartFile file) {
        Content content = contentRepository.findById(id).orElseThrow(()-> new RuntimeException("Content not found"));
        Content updatedContent = contentMapper.updateContentFromDto(contentDto,content);
        return contentRepository.save(updatedContent);
    }

}
