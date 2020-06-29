package com.traderepublic.quotesaggregator.repository;

import com.traderepublic.quotesaggregator.model.repository.InstrumentDao;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstrumentRepository extends CrudRepository<InstrumentDao, String> {
}
