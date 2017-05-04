package io.fossa.service;

import org.json.simple.JSONObject;

public class Revision {
    private Locator locator;
    private Integer unresolvedIssueCount;

    public Revision(Locator locator, Integer unresolvedIssueCount) {
        this.locator = locator;
        this.unresolvedIssueCount = unresolvedIssueCount;
    }

    public Locator getLocator() {
        return locator;
    }

    public void setLocator(Locator locator) {
        this.locator = locator;
    }

    public int getUnresolvedIssueCount() {
        return unresolvedIssueCount;
    }

    public void setUnresolvedIssueCount(int unresolvedIssueCount) {
        this.unresolvedIssueCount = unresolvedIssueCount;
    }

    public boolean isFinishedScanning() {
        return this.unresolvedIssueCount != null;
    }

    public static class Builder {
        private Locator locator;
        private Integer unresolvedIssueCount;

        public Builder() {}

        public void setLocator(Locator locator) {
            this.locator = locator;
        }

        public void setUnresolvedIssueCount(int unresolvedIssueCount) {
            this.unresolvedIssueCount = unresolvedIssueCount;
        }

        public void fromJSON(JSONObject o) {
            this.setLocator(Locator.fromLocator((String)o.get("locator")));
            this.setUnresolvedIssueCount((Integer)o.get("unresolved_issue_count"));
        }

        public Revision build() {
            return new Revision(locator, unresolvedIssueCount);
        }
    }
}
