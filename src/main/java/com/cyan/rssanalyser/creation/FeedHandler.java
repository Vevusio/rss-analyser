package com.cyan.rssanalyser.creation;

import com.cyan.rssanalyser.persistence.Item;
import com.stoyanr.wordcounter.WordCounts;
import com.stoyanr.wordcounter.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.lang.invoke.MethodHandles;

class FeedHandler extends DefaultHandler {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final AnalysisContext analysisContext;
    private final FeedWordUtils feedWordUtils;

    private String characters;
    private Item item;
    private String language;

    public FeedHandler(AnalysisContext analysisContext, FeedWordUtils feedWordUtils) {
        this.analysisContext = analysisContext;
        this.feedWordUtils = feedWordUtils;
    }

    public void characters(char[] ch, int start, int length) {
        characters = new String(ch, start, length);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if ("item".equals(qName)) {
            item = new Item();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("language".equals(qName)) {
            language = characters;

        } else if ("item".equals(qName)) {
            analyseItem();

        } else if (item != null) {
            switch (qName) {
                case "title":
                    item.setTitle(characters);
                    break;
                case "link":
                    item.setLink(characters);
                    break;
            }
        }
    }

    private void analyseItem() {
        if(!isValidItem(item)) {
            return;
        }

        analysisContext.addItem(item);

        WordCounts wordCounts = WordUtils.countWords(item.getTitle(), feedWordUtils.retrieveCharacterPredicate(), String::toLowerCase);
        wordCounts.forEachInRange(0, wordCounts.getSize(), (word, count) -> {
            if (feedWordUtils.isValidWord(word, language)) {
                analysisContext.addOccurence(word, item);
            }
        });
    }

    private boolean isValidItem(Item item) {
        boolean mandatoryPropertiesFilled = item.getLink() != null && item.getTitle() != null;
        if (!mandatoryPropertiesFilled) {
            logger.info("Skipping invalid item=" + item);
            return false;
        }

        if (item.getLink().length() > Item.LINK_MAX_LENGTH) {
            logger.warn("Item link exceeds max length. link=" + item.getLink());
            return false;
        }

        if (item.getTitle().length() > Item.TITLE_MAX_LENGTH) {
            logger.warn("Item title exceeds max length. title=" + item.getTitle());
            return false;
        }

        return true;
    }
}