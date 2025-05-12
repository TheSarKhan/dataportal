package org.example.dataprotal.repository.page;
import org.example.dataprotal.model.page.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByPageName(String pageName);
}
