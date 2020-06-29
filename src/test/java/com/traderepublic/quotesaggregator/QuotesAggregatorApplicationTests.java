package com.traderepublic.quotesaggregator;

import com.traderepublic.quotesaggregator.comms.controller.QuoteAggregatorController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class QuotesAggregatorApplicationTests {
	@Autowired
	private QuoteAggregatorController quoteAggregatorController;

	@Test
	void contextLoads() {
		assertThat(quoteAggregatorController).isNotNull();
	}

}
