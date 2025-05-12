package org.example.dataprotal.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.dataprotal.dto.CardDto;
import org.example.dataprotal.model.page.Card;
import org.example.dataprotal.model.page.SubContent;
import org.example.dataprotal.service.CardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/v1/card")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;

    @GetMapping
    @Operation(description = "Səhifə adı ilə cardları almaq üçün endpoint")
    public ResponseEntity<List<Card>>getCardsByPageName(@RequestParam String pageName) {
        return ResponseEntity.ok(cardService.getCardsByPageName(pageName));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Card> getCardById(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.getCardById(id));
    }


    @PostMapping
    @Operation(description = "Yeni cardlar əlavə etmək üçün endpoint")
    public ResponseEntity<Card>createCard(@RequestBody CardDto cardDto) {
        final var createdCard = cardService.createCard(cardDto);
        final var location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/{Id}").build(createdCard.getId());
        return ResponseEntity.created(location).body(createdCard);
    }

    @PutMapping("/{id}")
    @Operation(description = "Cardlar güncəlləmək üçün endpoint")
    public ResponseEntity<Card> updateCard(@PathVariable Long id, @RequestBody CardDto cardDto) {
        final var card = cardService.updateCard(id,cardDto);
        final var location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/{Id}").build(card.getId());
        return ResponseEntity.created(location).body(card);
    }

}
