package org.example.dataprotal.repository.chatbot;

import org.example.dataprotal.model.chatbot.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {}
