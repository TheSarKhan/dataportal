package org.example.dataprotal.repository.dataset;

import org.example.dataprotal.model.dataset.DataSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataSetRepository extends JpaRepository<DataSet, Long> {
}
