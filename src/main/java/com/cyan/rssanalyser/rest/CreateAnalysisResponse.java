package com.cyan.rssanalyser.rest;

class CreateAnalysisResponse {
    private String analysisId;

    public CreateAnalysisResponse(String analysisId) {
        this.analysisId = analysisId;
    }

    public String getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(String analysisId) {
        this.analysisId = analysisId;
    }
}
