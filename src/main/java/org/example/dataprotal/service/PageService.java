package org.example.dataprotal.service;

import lombok.RequiredArgsConstructor;
import org.example.dataprotal.dto.PageBasicDto;
import org.example.dataprotal.dto.PageFullDto;
import org.example.dataprotal.mapper.PageMapper;
import org.example.dataprotal.model.page.Page;
import org.example.dataprotal.model.page.Subpage;
import org.example.dataprotal.repository.page.PageRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PageService {
    private final PageRepository pageRepository;
    private final PageMapper pageMapper;

    public Page createPage(PageFullDto pageFullDto) {
        Page page = pageMapper.dtoToEntity(pageFullDto);
        return pageRepository.save(page);
    }

    public Page updatePage(Long id, PageBasicDto pageBasicDto) {
        Page page = pageRepository.findById(id).orElseThrow(()->new RuntimeException("Page not found"));
        Page updatedPage = pageMapper.updatePageFromDto(pageBasicDto,page);
        return pageRepository.save(updatedPage);
    }

    public Page addSubPage(Long id, Subpage subpage) {
        Page page = pageRepository.findById(id).orElseThrow(()->new RuntimeException("Page not found"));
        page.getSubpageList().add(subpage);
        return pageRepository.save(page);
    }

    public Page getPageByName(String pageName) {
        return pageRepository.findByName(pageName).orElseThrow(()-> new RuntimeException("Page not found"));
    }
}
