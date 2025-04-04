package org.example.dataprotal.repository.company;

import org.example.dataprotal.model.company.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
