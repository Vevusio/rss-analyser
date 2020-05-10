package com.cyan.rssanalyser.creation;

import com.cyan.rssanalyser.persistence.Item;
import com.cyan.rssanalyser.persistence.Word;
import com.cyan.rssanalyser.persistence.WordOccurence;

import java.util.*;

class AnalysisContext {
    private final Map<String, Word> words = Collections.synchronizedMap(new HashMap<>());
    private final List<Item> items = Collections.synchronizedList(new ArrayList<>());
    private final List<WordOccurence> wordOccurences = Collections.synchronizedList(new ArrayList<>());

    public void addItem(Item item) {
        items.add(item);
    }

    public void addOccurence(String wordString, Item item) {
        registerWord(wordString);

        Word word = words.get(wordString);
        WordOccurence wordOccurence = new WordOccurence(word, item);
        wordOccurences.add(wordOccurence);
    }

    private void registerWord(String wordString) {
        synchronized (words) {
            if (!words.containsKey(wordString)) {
                words.put(wordString, new Word(wordString));
            }
        }
    }

    public Map<String, Word> getWords() {
        return words;
    }

    public List<Item> getItems() {
        return items;
    }

    public List<WordOccurence> getWordOccurences() {
        return wordOccurences;
    }
}