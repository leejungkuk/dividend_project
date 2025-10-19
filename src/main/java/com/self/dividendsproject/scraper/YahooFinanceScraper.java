package com.self.dividendsproject.scraper;

import com.self.dividendsproject.model.Company;
import com.self.dividendsproject.model.Dividend;
import com.self.dividendsproject.model.ScrapeResult;
import com.self.dividendsproject.model.constants.Month;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScraper implements Scraper {
    private static final String STATICS_URL = "https://finance.yahoo.com/quote/%s/history/?frequency=1mo&period1=%d&period2=%d";
    private static final long START_TIME = 86400; // 60 * 60 * 24
    private static final String SUMMERY_URL = "https://finance.yahoo.com/quote/%s?p=%s";

    @Override
    public ScrapeResult scrap(Company company) {
        var scrapeResult = new ScrapeResult();
        scrapeResult.setCompany(company);
        try {
            long now = System.currentTimeMillis() / 1000;

            String url = String.format(STATICS_URL, company.getTicker(), START_TIME, now);
            Connection connection = Jsoup.connect(url);
            Document document = connection.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                            "AppleWebKit/537.36 (KHTML, like Gecko) " +
                            "Chrome/118.0.5993.118 Safari/537.36")
                    .timeout(10 * 1000).get();

            Elements parsingDivs = document.getElementsByAttributeValue("data-testid", "history-table");
            Element tableEle = parsingDivs.get(0);

            Element tbody = tableEle.getElementsByTag("tbody").get(0);
            List<Dividend> dividends = new ArrayList<>();

            for (Element e : tbody.children()) {
                String txt = e.text();

                if (!txt.endsWith("Dividend")) {
                    continue;
                }

                String[] splits = txt.split(" ");
                int month = Month.strToNumber(splits[0]);
                int day = Integer.valueOf(splits[1].replace(",", ""));
                int year = Integer.valueOf(splits[2]);
                String dividend = splits[3];

                if (month < 0) {
                    throw new RuntimeException("Unexpected Month enum value -> " + splits[0]);
                }

                dividends.add(new Dividend(LocalDateTime.of(year, month, day, 0, 0), dividend));


//                System.out.println(year + "/" + month + "/" + day + " -> " + dividend);
            }
            scrapeResult.setDividendEntities(dividends);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return scrapeResult;
    }

    @Override
    public Company scrapCompanyByTicker(String ticker) {
        String url = String.format(SUMMERY_URL, ticker, ticker);

        try {
            Document document = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) " +
                    "Chrome/118.0.5993.118 Safari/537.36")
                    .timeout(10 * 1000).get();
            Element titleEle = document.getElementById("nimbus-app").getElementsByTag("h1").get(0);

            String title = titleEle.text().split("\\(")[0].trim();

            return new Company(ticker, title);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
