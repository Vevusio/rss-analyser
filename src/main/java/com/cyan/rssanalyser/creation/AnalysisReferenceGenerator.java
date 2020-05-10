package com.cyan.rssanalyser.creation;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
class AnalysisReferenceGenerator {
    private AtomicInteger analysisCount = new AtomicInteger(0);

    public String generate() {
        return "analysis_" + analysisCount.incrementAndGet();
    }
}
