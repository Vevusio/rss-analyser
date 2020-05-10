package com.cyan.rssanalyser.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(AnalysisRepository.class)
public class AnalysisRepositoryIntegrationTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AnalysisRepository analysisRepository;

    @Test
    public void retrieveTopOccuringWordsInAnalysis_finds_top_word_in_analysis() {
        Word alice = persistWord("alice");
        Item aliceItem = persistItem("good morning Alice", "https://feed.com/alice1");
        persistOccurence(alice, aliceItem);

        Word bob = persistWord("bob");
        Item bobItem1 = persistItem("hello bob", "https://feed.com/bob1");
        Item bobItem2 = persistItem("hi Bob", "https://feed.com/bob2");
        persistOccurence(bob, bobItem1);
        persistOccurence(bob, bobItem2);

        persistAnalysis("analysis_reference", aliceItem, bobItem1, bobItem2);

        List<Word> topWords = analysisRepository.retrieveTopOccuringWordsInAnalysis(1, "analysis_reference");
        assertThat(topWords).containsExactly(bob);
    }

    @Test
    public void retrieveTopOccuringWordsInAnalysis_finds_all_top_word_in_analysis_where_limit_is_higher_than_analysis_words() {
        Word alice = persistWord("alice");
        Item aliceItem = persistItem("good morning Alice", "https://feed.com/alice1");
        persistOccurence(alice, aliceItem);

        Word bob = persistWord("bob");
        Item bobItem1 = persistItem("hello bob", "https://feed.com/bob1");
        Item bobItem2 = persistItem("hi Bob", "https://feed.com/bob2");
        persistOccurence(bob, bobItem1);
        persistOccurence(bob, bobItem2);

        persistAnalysis("analysis_reference", aliceItem, bobItem1, bobItem2);

        List<Word> topWords = analysisRepository.retrieveTopOccuringWordsInAnalysis(2, "analysis_reference");
        assertThat(topWords).containsExactly(bob, alice);
    }

    @Test
    public void retrieveTopOccuringWordsInAnalysis_finds_nothing_for_non_existing_analysis_reference() {
        Word alice = persistWord("alice");
        Item aliceItem = persistItem("good morning Alice", "https://feed.com/alice1");
        persistOccurence(alice, aliceItem);

        Word bob = persistWord("bob");
        Item bobItem = persistItem("hello bob", "https://feed.com/bob1");
        persistOccurence(bob, bobItem);

        persistAnalysis("analysis_reference", aliceItem, bobItem);

        List<Word> topWords = analysisRepository.retrieveTopOccuringWordsInAnalysis(1, "invalid_reference");
        assertThat(topWords).isEmpty();
    }

    @Test
    public void retrieveItemsForWordInAnalysis_finds_all_items_with_word_occurrence() {
        Word alice = persistWord("alice");
        Item aliceItem = persistItem("good morning Alice", "https://feed.com/alice1");
        persistOccurence(alice, aliceItem);

        Word bob = persistWord("bob");
        Item bobItem1 = persistItem("hello bob", "https://feed.com/bob1");
        Item bobItem2 = persistItem("hi Bob", "https://feed.com/bob2");
        persistOccurence(bob, bobItem1);
        persistOccurence(bob, bobItem2);

        persistAnalysis("analysis_reference", aliceItem, bobItem1, bobItem2);

        List<Item> items = analysisRepository.retrieveItemsForWordInAnalysis("bob", "analysis_reference");
        assertThat(items).containsExactlyInAnyOrder(bobItem1, bobItem2);
    }

    private void persistOccurence(Word coolness, Item iHowCool) {
        entityManager.persist(new WordOccurence(coolness, iHowCool));
    }

    private Item persistItem(String s, String s2) {
        Item iHowCool = new Item(s, s2);
        entityManager.persist(iHowCool);
        return iHowCool;
    }

    private Word persistWord(String coolness2) {
        Word coolness = new Word(coolness2);
        entityManager.persist(coolness);
        return coolness;
    }

    private void persistAnalysis(String reference, Item... items) {
        Analysis analysis = new Analysis("analysis_reference");
        entityManager.persist(analysis);
        for (Item item : items) {
            entityManager.persist(new AnalysisItem(analysis, item));
        }
    }
}