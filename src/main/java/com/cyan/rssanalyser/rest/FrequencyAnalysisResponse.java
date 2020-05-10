package com.cyan.rssanalyser.rest;

import java.util.ArrayList;
import java.util.List;

class FrequencyAnalysisResponse {
    private List<WordOccurencesDto> frequentWords = new ArrayList<>();

    public List<WordOccurencesDto> getFrequentWords() {
        return frequentWords;
    }

    public void setFrequentWords(List<WordOccurencesDto> frequentWords) {
        this.frequentWords = frequentWords;
    }
}