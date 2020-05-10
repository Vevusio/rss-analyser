package com.cyan.rssanalyser.persistence;

import javax.persistence.*;

@Entity
public class Item {
	public static final int LINK_MAX_LENGTH = 1024;
	public static final int TITLE_MAX_LENGTH = 255;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Column(length = TITLE_MAX_LENGTH, nullable = false)
	private String title;

	@Column(length = LINK_MAX_LENGTH, nullable = false)
	private String link;

	public Item() {}

	public Item(String title, String link) {
		this.title = title;
		this.link = link;
	}

	@Override
	public String toString() {
		return "Item{" +
				"id=" + id +
				", title='" + title + '\'' +
				", link='" + link + '\'' +
				'}';
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
}
