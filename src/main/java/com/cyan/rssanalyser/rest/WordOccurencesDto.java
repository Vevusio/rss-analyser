package com.cyan.rssanalyser.rest;

import java.util.ArrayList;
import java.util.List;

class WordOccurencesDto {
    private String word;
    private List<ItemDto> seenIn = new ArrayList<>();

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public List<ItemDto> getSeenIn() {
        return seenIn;
    }

    public void setSeenIn(List<ItemDto> seenIn) {
        this.seenIn = seenIn;
    }
}