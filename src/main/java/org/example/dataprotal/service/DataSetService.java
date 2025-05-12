package org.example.dataprotal.service;

import lombok.RequiredArgsConstructor;
import org.example.dataprotal.dto.DataSetDto;
import org.example.dataprotal.mapper.DataSetMapper;
import org.example.dataprotal.model.dataset.DataSet;
import org.example.dataprotal.repository.dataset.DataSetRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class DataSetService {
    private final DataSetRepository repository;
    private final DataSetMapper dataSetMapper;

    public DataSet createDataSet(DataSetDto dataSetDto, MultipartFile file,MultipartFile img) {
        DataSet dataSet = dataSetMapper.dtoToEntity(dataSetDto);
        return repository.save(dataSet);
    }
}
