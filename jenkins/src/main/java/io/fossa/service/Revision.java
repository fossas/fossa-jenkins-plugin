package io.fossa.service;

import org.json.simple.JSONObject;

public class Revision {
    private Locator locator;
    private Long unresolvedIssueCount;

    public Revision(Locator locator, Long unresolvedIssueCount) {
        this.locator = locator;
        this.unresolvedIssueCount = unresolvedIssueCount;
    }

    public Locator getLocator() {
        return locator;
    }

    public Long getUnresolvedIssueCount() {
        return unresolvedIssueCount;
    }

    public boolean isFinishedScanning() {
        return this.unresolvedIssueCount != null;
    }

    public static class Builder {
        private Locator locator;
        private Long unresolvedIssueCount;

        public Builder() {}

        public void setLocator(Locator locator) {
            this.locator = locator;
        }

        public void setUnresolvedIssueCount(Long unresolvedIssueCount) {
            this.unresolvedIssueCount = unresolvedIssueCount;
        }

        public void fromJSON(JSONObject o) {
            this.setLocator(Locator.fromLocator((String)o.get("locator")));
            this.setUnresolvedIssueCount((Long)o.get("unresolved_issue_count"));
        }

        public Revision build() {
            return new Revision(locator, unresolvedIssueCount);
        }
    }
}
