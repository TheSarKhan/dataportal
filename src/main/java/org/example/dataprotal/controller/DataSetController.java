package org.example.dataprotal.controller;

import lombok.RequiredArgsConstructor;
import org.example.dataprotal.dto.DataSetDto;
import org.example.dataprotal.model.dataset.DataSet;
import org.example.dataprotal.service.DataSetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/dataset")
@RequiredArgsConstructor
public class DataSetController {
    private final DataSetService service;

    @PostMapping
    public ResponseEntity<DataSet> createDataSet(@RequestPart("request") DataSetDto dataSetDto,
                                                @RequestPart(required = false) MultipartFile file,
                                                @RequestPart(required = false) MultipartFile img) {
     final var createDataSet = service.createDataSet(dataSetDto,file,img);
     final var location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").build(createDataSet.getId());
     return ResponseEntity.created(location).body(createDataSet);
    }
    
}
