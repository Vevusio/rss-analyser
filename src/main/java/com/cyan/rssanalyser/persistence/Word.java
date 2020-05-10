package com.cyan.rssanalyser.persistence;

import javax.persistence.*;

@Entity
public class Word {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Column(nullable = false)
	private String word;

	protected Word() {}

	public Word(String word) {
		this.word = word;
	}

	@Override
	public String toString() {
		return "Word{" +
				"word='" + word + '\'' +
				'}';
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}
}
