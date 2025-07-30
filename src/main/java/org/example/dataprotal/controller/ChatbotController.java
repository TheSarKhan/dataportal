package org.example.dataprotal.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.dataprotal.dto.request.CategoryRequest;
import org.example.dataprotal.model.chatbot.Category;
import org.example.dataprotal.model.chatbot.Question;
import org.example.dataprotal.repository.chatbot.CategoryRepository;
import org.example.dataprotal.repository.chatbot.QuestionRepository;
import org.example.dataprotal.service.BotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/chatbot")
@RequiredArgsConstructor
public class ChatbotController {
    private final BotService botService;
    private final CategoryRepository categoryRepository;
    private final QuestionRepository questionRepository;

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody Map<String, String> body) {
        String userMessage = body.get("question");  // 🔁 "message" değil, "question"
        if (userMessage == null || userMessage.isBlank()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Sual boşdur"));
        }

        String botResponse = botService.askBot(userMessage.toLowerCase());
        return ResponseEntity.ok(Collections.singletonMap("response", botResponse));
    }


    @GetMapping("/getAllCategories")
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @GetMapping("/getAllQuestions")
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    @PostMapping("/category/add")
    public ResponseEntity<Category> addCategory(@RequestBody CategoryRequest categoryRequest) {
        Category newCategory=new Category();
        newCategory.setCategoryName(categoryRequest.getCategoryName());
        Category savedCategory = categoryRepository.save(newCategory);
        return ResponseEntity.ok(savedCategory);
    }

    @PostMapping("/question/add")
    public ResponseEntity<?> addQuestion(@RequestBody QuestionRequest request) {
        Optional<Category> categoryOpt = categoryRepository.findById(request.getCategoryId());

        if (categoryOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Category bulunamadı.");
        }

        Question question = new Question();
        question.setCategory(categoryOpt.get());
        question.setQuestion(request.getQuestion());
        question.setAnswer(request.getAnswer());

        Question savedQuestion = questionRepository.save(question);
        return ResponseEntity.ok(savedQuestion);
    }

    @Data
    public static class QuestionRequest {
        private Long categoryId;
        private String question;
        private String answer;
    }
}
