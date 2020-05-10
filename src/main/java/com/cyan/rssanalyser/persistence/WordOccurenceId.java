package com.cyan.rssanalyser.persistence;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class WordOccurenceId implements Serializable {

	private Long wordId;

	private Long itemId;

    protected WordOccurenceId() {
    }

    public WordOccurenceId(Long wordId, Long itemId) {
        this.wordId = wordId;
        this.itemId = itemId;
    }

    public Long getWordId() {
        return wordId;
    }

    public void setWordId(Long wordId) {
        this.wordId = wordId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordOccurenceId that = (WordOccurenceId) o;
        return Objects.equals(wordId, that.wordId) &&
                Objects.equals(itemId, that.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wordId, itemId);
    }
}