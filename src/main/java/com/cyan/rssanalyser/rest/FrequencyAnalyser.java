package com.cyan.rssanalyser.rest;

import com.cyan.rssanalyser.persistence.AnalysisRepository;
import com.cyan.rssanalyser.persistence.Item;
import com.cyan.rssanalyser.persistence.Word;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FrequencyAnalyser {

    private final AnalysisRepository analysisRepository;

    public FrequencyAnalyser(AnalysisRepository analysisRepository) {
        this.analysisRepository = analysisRepository;
    }

    public FrequencyAnalysisResponse createFrequencyAnalysis(int topWordsLimit, String analysisReference) {
        FrequencyAnalysisResponse frequencyAnalysis = new FrequencyAnalysisResponse();

        List<Word> topWords = analysisRepository.retrieveTopOccuringWordsInAnalysis(topWordsLimit, analysisReference);
        for (Word topWord : topWords) {
            WordOccurencesDto wordFrequency = new WordOccurencesDto();
            wordFrequency.setWord(topWord.getWord());

            List<Item> items = analysisRepository.retrieveItemsForWordInAnalysis(topWord.getWord(), analysisReference);
            List<ItemDto> wordSeenInItems = items.stream().map(i -> new ItemDto(i.getTitle(), i.getLink())).collect(Collectors.toList());
            wordFrequency.setSeenIn(wordSeenInItems);

            frequencyAnalysis.getFrequentWords().add(wordFrequency);
        }

        return frequencyAnalysis;
    }
}
