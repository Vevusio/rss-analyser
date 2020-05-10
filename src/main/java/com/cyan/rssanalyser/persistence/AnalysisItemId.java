package com.cyan.rssanalyser.persistence;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AnalysisItemId implements Serializable {

	private Long analysisId;

	private Long itemId;

    protected AnalysisItemId() {
    }

    public AnalysisItemId(Long analysisId, Long itemId) {
        this.analysisId = analysisId;
        this.itemId = itemId;
    }

    public Long getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(Long analysisId) {
        this.analysisId = analysisId;
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
        AnalysisItemId that = (AnalysisItemId) o;
        return Objects.equals(analysisId, that.analysisId) &&
                Objects.equals(itemId, that.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(analysisId, itemId);
    }
}