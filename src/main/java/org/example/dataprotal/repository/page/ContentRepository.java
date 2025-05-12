package org.example.dataprotal.repository.page;

import org.example.dataprotal.model.page.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    Optional<Content> findByPageName(String pageName);
}
