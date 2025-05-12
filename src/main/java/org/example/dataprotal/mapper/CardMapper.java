package org.example.dataprotal.mapper;

import org.example.dataprotal.dto.CardDto;
import org.example.dataprotal.model.page.Card;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CardMapper {
    Card dtoToEntity(CardDto cardDto);
    Card updateCardFromDto(CardDto basicCardDto, @MappingTarget Card card);
}
