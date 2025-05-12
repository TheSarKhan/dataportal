package org.example.dataprotal.mapper;
import org.example.dataprotal.dto.PageBasicDto;
import org.example.dataprotal.dto.PageFullDto;
import org.example.dataprotal.model.page.Page;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PageMapper {
    Page dtoToEntity(PageFullDto dto);
    Page updatePageFromDto(PageBasicDto dto, @MappingTarget Page page);
}
