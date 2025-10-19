package com.self.dividendsproject.scheduler;

import com.self.dividendsproject.model.Company;
import com.self.dividendsproject.model.ScrapeResult;
import com.self.dividendsproject.model.constants.CacheKey;
import com.self.dividendsproject.persist.CompanyRepository;
import com.self.dividendsproject.persist.DividendRepository;
import com.self.dividendsproject.persist.entity.CompanyEntity;
import com.self.dividendsproject.persist.entity.DividendEntity;
import com.self.dividendsproject.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@EnableCaching
@AllArgsConstructor
public class ScraperScheduler {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    private final Scraper yahooFinanceScraper;

//    @Scheduled(fixedRate = 1000)
//    public void test1() throws InterruptedException {
//        Thread.sleep(10000);// 10초간 일시정지
//        System.out.println(Thread.currentThread().getName() + " -> 테스트1 : " + LocalDateTime.now());
//    }
//
//    @Scheduled(fixedRate = 1000)
//    public void test2() throws InterruptedException {
//        System.out.println(Thread.currentThread().getName() + " -> 테스트2 : " + LocalDateTime.now());
//    }

    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    @CacheEvict(value= CacheKey.KEY_FINANCE, allEntries = true)
    public void yahooFinanceScheduling() {
        log.info("Scraper Scheduler started");
        // 저장된 회사 목록을 조회
        List<CompanyEntity> companies = companyRepository.findAll();
        // 회사마다 배당금 정보를 새로 스크래핑
        for(var company : companies) {
            log.info("Scraper Scheduler started -> " + company.getName());
            ScrapeResult scrapeResult = yahooFinanceScraper.scrap(new Company(company.getTicker(), company.getName()));

            //스크래핑한 배당금 정보 중 데이터베이스에 없는 값은 저장
            scrapeResult.getDividendEntities().stream().map(e -> new DividendEntity(company.getId(), e))
                    .forEach(e -> {
                        boolean exists = dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
                        if(!exists) {
                            dividendRepository.save(e);
                            log.info("insert new dividend -> " + e.toString());
                        }
                    });
            // 연속적으로 스크래핑 대상 사이트 서버에 요청 날리지 않도록 일시정지
            try{
                Thread.sleep(3000); // 3초
            }catch (InterruptedException e){
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }

        }

    }
}
