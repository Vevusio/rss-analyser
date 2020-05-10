package com.cyan.rssanalyser.persistence;

import javax.persistence.*;

@Entity
public class Analysis {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Column(unique = true, nullable = false)
	private String reference;

	public Analysis() {}

	public Analysis(String reference) {
		this.reference = reference;
	}

	@Override
	public String toString() {
		return "Analysis{" +
				"reference='" + reference + '\'' +
				'}';
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}
}
