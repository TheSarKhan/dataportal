package org.example.dataprotal.service;

import org.example.dataprotal.dto.request.CompanyRequest;
import org.example.dataprotal.model.company.Company;

import java.io.IOException;

public interface CompanyService {
    Company addCompany(CompanyRequest companyRequest);
     byte[] exportCompaniesToExcel() throws IOException;
}
