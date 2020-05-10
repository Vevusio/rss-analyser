package com.cyan.rssanalyser.persistence;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import java.util.Objects;

@Entity
public class WordOccurence {

	@EmbeddedId
	private WordOccurenceId id;

	@ManyToOne
	@MapsId("wordId")
	private Word word;

	@ManyToOne
	@MapsId("itemId")
	private Item item;

	private WordOccurence() {}

	public WordOccurence(Word word, Item item) {
		this.word = word;
		this.item = item;
		this.id = new WordOccurenceId(word.getId(), item.getId());
	}

	public WordOccurenceId getId() {
		return id;
	}

	public void setId(WordOccurenceId id) {
		this.id = id;
	}

	public Word getWord() {
		return word;
	}

	public void setWord(Word word) {
		this.word = word;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		WordOccurence that = (WordOccurence) o;
		return word.equals(that.word) &&
				item.equals(that.item);
	}

	@Override
	public int hashCode() {
		return Objects.hash(word, item);
	}
}
