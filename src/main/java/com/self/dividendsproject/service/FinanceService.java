package com.self.dividendsproject.service;

import com.self.dividendsproject.exception.impl.NoCompanyException;
import com.self.dividendsproject.model.Company;
import com.self.dividendsproject.model.Dividend;
import com.self.dividendsproject.model.ScrapeResult;
import com.self.dividendsproject.model.constants.CacheKey;
import com.self.dividendsproject.persist.CompanyRepository;
import com.self.dividendsproject.persist.DividendRepository;
import com.self.dividendsproject.persist.entity.CompanyEntity;
import com.self.dividendsproject.persist.entity.DividendEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)
    public ScrapeResult getDividendByCompanyName(String companyName) {
        log.info("search company - > " + companyName);
        // 1. 회사명을 기준으로 회사 정보를 조회
        CompanyEntity company = companyRepository.findByName(companyName)
                .orElseThrow(() -> new NoCompanyException());

        // 2. 조회된 회사 ID로 배당금 정보를 조회
        List<DividendEntity> dividendEntityList = dividendRepository.findAllByCompanyId(company.getId());

        // 3. 결과 조합 후 반환
        List<Dividend> dividends = dividendEntityList.stream()
                                    .map(e -> new Dividend(e.getDate(), e.getDividend()))
                                    .collect(Collectors.toList());

        return new ScrapeResult(new Company(company.getTicker(), company.getName()), dividends);
    }
}
