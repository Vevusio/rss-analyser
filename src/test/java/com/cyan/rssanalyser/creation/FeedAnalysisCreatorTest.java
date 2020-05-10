package com.cyan.rssanalyser.creation;

import com.cyan.rssanalyser.persistence.AnalysisRepository;
import com.cyan.rssanalyser.persistence.Item;
import com.cyan.rssanalyser.persistence.Word;
import com.cyan.rssanalyser.persistence.WordOccurence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedAnalysisCreatorTest {
    private FeedAnalysisCreator feedAnalysisCreator;

    @Mock
    AnalysisRepository analysisRepository;

    @Mock
    FeedWordUtils feedWordUtils;

    @Mock
    AnalysisReferenceGenerator analysisReferenceGenerator;

    @BeforeEach
    public void setup() {
        feedAnalysisCreator = new FeedAnalysisCreator(analysisRepository, feedWordUtils, analysisReferenceGenerator, 10);

        when(feedWordUtils.retrieveCharacterPredicate()).thenReturn(Character::isLetterOrDigit);
        when(feedWordUtils.isValidWord(anyString(), nullable(String.class))).thenReturn(true);

        when(analysisReferenceGenerator.generate()).thenReturn("");
    }

    @Test
    public void simple_feed_analysed_correctly() {
        feedAnalysisCreator.analyseRssFeeds(singletonList(feedUrl("feeds/simle_feed.xml")));

        AnalysisVerification analysisVerification = verifyCreatedAnalysis();

        analysisVerification.assertItemTitles("one two", "two three");
        analysisVerification.assertWords("one", "two", "three");
        analysisVerification.assertWordOccurence("one", "one two");
        analysisVerification.assertWordOccurence("two", "one two");
        analysisVerification.assertWordOccurence("two", "two three");
        analysisVerification.assertWordOccurence("three", "two three");
        analysisVerification.assertWordOccurencesSize(4);
    }

    @Test
    public void feed_with_bob_and_alice_analysed_where__and__is_blacklisted_word() {
        when(feedWordUtils.isValidWord( "and", "en-US")).thenReturn(false);

        feedAnalysisCreator.analyseRssFeeds(singletonList(feedUrl("feeds/en-us_bob_and_alice.xml")));

        AnalysisVerification analysisVerification = verifyCreatedAnalysis();

        analysisVerification.assertItemTitles("bob and alice");
        analysisVerification.assertWords("bob", "alice");
        analysisVerification.assertWordOccurence("bob", "bob and alice");
        analysisVerification.assertWordOccurence("alice", "bob and alice");
        analysisVerification.assertWordOccurencesSize(2);
    }

    @Test
    public void invalid_items_in_feed_are_silently_ignored() {
        feedAnalysisCreator.analyseRssFeeds(singletonList(feedUrl("feeds/invalid_item_feed.xml")));

        AnalysisVerification analysisVerification = verifyCreatedAnalysis();

        analysisVerification.assertItemTitles("finally");
        analysisVerification.assertWords("finally");
        analysisVerification.assertWordOccurence("finally", "finally");
        analysisVerification.assertWordOccurencesSize(1);
    }

    @Test
    public void partially_malformed_feed_analysed_tolerating_errors() {
        feedAnalysisCreator.analyseRssFeeds(singletonList(feedUrl("feeds/partially_malformed_feed.xml")));

        AnalysisVerification analysisVerification = verifyCreatedAnalysis();

        analysisVerification.assertItemTitles("tango", "bravo");
        analysisVerification.assertWords("tango", "bravo");
        analysisVerification.assertWordOccurence("tango", "tango");
        analysisVerification.assertWordOccurence("bravo", "bravo");
        analysisVerification.assertWordOccurencesSize(2);
    }

    @Test
    public void one_valid_and_one_broken_feed_analysed_tolerating_errors() {
        feedAnalysisCreator.analyseRssFeeds(asList(feedUrl("feeds/minimal_feed.xml"), "badProtocol:/invalid_feed"));

        AnalysisVerification analysisVerification = verifyCreatedAnalysis();

        analysisVerification.assertItemTitles("mini");
        analysisVerification.assertWords("mini");
        analysisVerification.assertWordOccurence("mini", "mini");
        analysisVerification.assertWordOccurencesSize(1);
    }

    private AnalysisVerification verifyCreatedAnalysis() {
        ArgumentCaptor<Collection> itemsCaptor = ArgumentCaptor.forClass(Collection.class);
        ArgumentCaptor<Collection> occurencesCaptor = ArgumentCaptor.forClass(Collection.class);
        ArgumentCaptor<Collection> wordsCaptor = ArgumentCaptor.forClass(Collection.class);

        verify(analysisRepository).createAnalysis(anyString(), itemsCaptor.capture(), wordsCaptor.capture(), occurencesCaptor.capture());

        AnalysisVerification analysisVerification = new AnalysisVerification();
        analysisVerification.items = itemsCaptor.getValue();
        analysisVerification.words = wordsCaptor.getValue();
        analysisVerification.occurences = occurencesCaptor.getValue();
        return analysisVerification;
    }

    private static class AnalysisVerification {
        Collection<Item> items;
        Collection<Word> words;
        Collection<WordOccurence> occurences;

        void assertItemTitles(String... titles) {
            assertThat(this.items).extracting(Item::getTitle).containsExactlyInAnyOrder(titles);
        }

        void assertWords(String... words) {
            assertThat(this.words).extracting(Word::getWord).containsExactlyInAnyOrder(words);
        }

        void assertWordOccurence(String word, String itemTitle) {
            this.occurences.stream()
                    .filter(occurence ->
                            word.equals(occurence.getWord().getWord()) &&
                            itemTitle.equals(occurence.getItem().getTitle()))
                    .findAny()
                    .orElseThrow(() -> new AssertionError(
                            "Expected word='" + word + "' " +
                                    "in itemTitel='" + itemTitle + "'. " +
                                    "missing from: " + occurences));
        }

        void assertWordOccurencesSize(int size) {
            assertThat(this.occurences).hasSize(size);
        }
    }

    private String feedUrl(String feed) {
        return getClass().getClassLoader().getResource(feed).toExternalForm();
    }
}