package com.cyan.rssanalyser.rest;

import com.cyan.rssanalyser.creation.FeedAnalysisCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/")
class AnalysisResource {
    private final FeedAnalysisCreator feedAnalysisCreator;
    private final FrequencyAnalyser frequencyAnalyser;
    private final int defaultTopWordsLimit;

    public AnalysisResource(FeedAnalysisCreator feedAnalysisCreator,
                            FrequencyAnalyser frequencyAnalyser,
                            @Value("${analyser.defaultTopWordsLimit}") int defaultTopWordsLimit) {
        this.feedAnalysisCreator = feedAnalysisCreator;
        this.frequencyAnalyser = frequencyAnalyser;
        this.defaultTopWordsLimit = defaultTopWordsLimit;
    }

    @PostMapping("/analyse/new")
    public CreateAnalysisResponse createNewAnalysis(@RequestBody @Valid NewAnalysisRequest newAnalysisRequest) {
        List<String> feedUrls = newAnalysisRequest.getFeedUrls();
        String analysisReference = feedAnalysisCreator.analyseRssFeeds(feedUrls).getReference();
        return new CreateAnalysisResponse(analysisReference);
    }

    @GetMapping("/frequency/{id}")
    public FrequencyAnalysisResponse retrieveFrequencyAnalysis(@PathVariable(name="id") String analysisReference) {
        FrequencyAnalysisResponse response =
                frequencyAnalyser.createFrequencyAnalysis(defaultTopWordsLimit, analysisReference);
        return response;
    }

}
