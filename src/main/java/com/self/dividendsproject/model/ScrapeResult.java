package com.self.dividendsproject.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class ScrapeResult {
    private Company company;
    private List<Dividend> dividendEntities;
    public ScrapeResult() {
        this.dividendEntities = new ArrayList<>();
    }
}
