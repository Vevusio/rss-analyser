package com.cyan.rssanalyser.persistence;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.List;

@Repository
@Transactional
public class AnalysisRepository {
    private final EntityManager entityManager;

    public AnalysisRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Analysis createAnalysis(String reference, Collection<Item> items, Collection<Word> words, Collection<WordOccurence> wordOccurences) {
        Analysis analysis = new Analysis(reference);
        entityManager.persist(analysis);
        items.forEach(entityManager::persist);
        items.forEach(item -> entityManager.persist(new AnalysisItem(analysis, item)));
        words.forEach(entityManager::persist);
        wordOccurences.forEach(entityManager::persist);

        return analysis;
    }

    public List<Word> retrieveTopOccuringWordsInAnalysis(int topWordsLimit, String analysisReference) {
        List<Word> topWords = entityManager.createQuery(
                "select w from Word w, WordOccurence wo, AnalysisItem ai where " +
                        "wo.word = w and " +
                        "wo.item = ai.item and " +
                        "ai.analysis.reference = :analysisReference " +
                        "group by w " +
                        "order by count(w) desc", Word.class)
                .setParameter("analysisReference", analysisReference)
                .setMaxResults(topWordsLimit)
                .getResultList();
        return topWords;
    }

    public List<Item> retrieveItemsForWordInAnalysis(String word, String analysisReference) {
        List<Item> items = entityManager.createQuery(
                "select ai.item from WordOccurence wo, AnalysisItem ai where " +
                        "wo.word.word = :word and " +
                        "wo.item = ai.item and " +
                        "ai.analysis.reference = :analysisReference", Item.class)
                .setParameter("analysisReference", analysisReference)
                .setParameter("word", word)
                .getResultList();
        return items;
    }
}
