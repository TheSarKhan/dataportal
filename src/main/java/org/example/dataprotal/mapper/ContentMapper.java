package org.example.dataprotal.mapper;

import org.example.dataprotal.dto.ContentDto;
import org.example.dataprotal.model.page.Content;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ContentMapper {
    Content dtoToEntity(ContentDto dto);
    Content updateContentFromDto(ContentDto basicContentDto, @MappingTarget Content content);
}
