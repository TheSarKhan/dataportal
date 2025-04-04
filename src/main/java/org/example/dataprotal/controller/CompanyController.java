package org.example.dataprotal.controller;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.dataprotal.dto.request.CompanyRequest;
import org.example.dataprotal.model.company.Company;
import org.example.dataprotal.repository.company.CompanyRepository;
import org.example.dataprotal.service.CompanyService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyRepository companyRepository;
    private final CompanyService companyService;

    @PostMapping("/add")
    public ResponseEntity<?> addCompany(@RequestBody CompanyRequest companyRequest) {
        companyService.addCompany(companyRequest);
        return ResponseEntity.status(201).body(companyRequest);
    }

    @GetMapping("/export-excel")
    public ResponseEntity<byte[]> exportCompaniesToExcel() throws IOException {
        byte[] excelData = companyService.exportCompaniesToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=companies.xlsx");

        return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
    }
}
