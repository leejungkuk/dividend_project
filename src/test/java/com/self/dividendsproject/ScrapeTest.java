package com.self.dividendsproject;

import com.self.dividendsproject.model.Company;
import com.self.dividendsproject.scraper.Scraper;
import com.self.dividendsproject.scraper.YahooFinanceScraper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ScrapeTest {

    @Test
    void scrapeTest() {
        Scraper scraper = new YahooFinanceScraper();
        var result = scraper.scrap(new Company("COKE", null));
        System.out.println(result);
    }

    @Test
    void titleTest() {
        Scraper scraper = new YahooFinanceScraper();
        var result = scraper.scrapCompanyByTicker("MMM");
        System.out.println(result);
    }
}
