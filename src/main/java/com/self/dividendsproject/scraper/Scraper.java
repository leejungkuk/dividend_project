package com.self.dividendsproject.scraper;

import com.self.dividendsproject.model.Company;
import com.self.dividendsproject.model.ScrapeResult;
import org.springframework.stereotype.Component;

public interface Scraper {
    Company scrapCompanyByTicker(String ticker);
    ScrapeResult scrap(Company company);
}
