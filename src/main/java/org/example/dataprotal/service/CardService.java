package org.example.dataprotal.service;

import lombok.RequiredArgsConstructor;
import org.example.dataprotal.dto.CardDto;
import org.example.dataprotal.mapper.CardMapper;
import org.example.dataprotal.model.page.Card;
import org.example.dataprotal.model.page.SubContent;
import org.example.dataprotal.repository.page.CardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    public Card createCard(CardDto cardDto) {
        Card card = cardMapper.dtoToEntity(cardDto);
        return cardRepository.save(card);
    }

    public Card updateCard(Long id, CardDto cardDto) {
        Card card = cardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Card not found"));
        Card saveCard = cardMapper.updateCardFromDto(cardDto, card);
        return cardRepository.save(saveCard);
    }

    public List<Card>getCardsByPageName(String pageName) {
        return cardRepository.findByPageName(pageName);
    }

    public Card getCardById(Long id) {
        return cardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Card not found"));
    }

    public Card addSubContent(Long id, SubContent subContent) {
        Card card = cardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Card not found"));
        card.getSubContents().add(subContent);
        return cardRepository.save(card);
    }

}
