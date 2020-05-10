package com.cyan.rssanalyser.rest;

import javax.validation.constraints.Size;
import java.util.List;

class NewAnalysisRequest {
    @Size(min = 2)
    private List<String> feedUrls;

    public List<String> getFeedUrls() {
        return feedUrls;
    }

    public void setFeedUrls(List<String> feedUrls) {
        this.feedUrls = feedUrls;
    }
}
