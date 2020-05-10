package com.cyan.rssanalyser.persistence;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@Entity
public class AnalysisItem {

	@EmbeddedId
	private AnalysisItemId id;

	@ManyToOne
	@MapsId("analysisId")
	private Analysis analysis;

	@ManyToOne
	@MapsId("itemId")
	private Item item;

	private AnalysisItem() {}

	public AnalysisItem(Analysis analysis, Item item) {
		this.analysis = analysis;
		this.item = item;
		this.id = new AnalysisItemId(analysis.getId(), item.getId());
	}

	public AnalysisItemId getId() {
		return id;
	}

	public void setId(AnalysisItemId id) {
		this.id = id;
	}

	public Analysis getAnalysis() {
		return analysis;
	}

	public void setAnalysis(Analysis analysis) {
		this.analysis = analysis;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}
}
