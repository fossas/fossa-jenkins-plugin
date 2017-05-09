package io.fossa.service;

import org.json.simple.JSONObject;

public class Build {
    private long id;
    private Locator locator;
    private String status;

    public Build(long id, Locator locator, String status) {
        this.id = id;
        this.locator = locator;
        this.status = status;
    }

    public Locator getLocator() {
        return locator;
    }

    public String getStatus() {
        return status;
    }

    public long getId() {
        return id;
    }

    public boolean isFinished() {
        return getStatus() != null && ( getStatus().equals("SUCCEEDED") || getStatus().equals("FAILED") );
    }

    public static class Builder {
        private long id;
        private Locator locator;
        private String status;

        public Builder() {}

        public void setId(long id) {
            this.id = id;
        }

        public void setLocator(Locator locator) {
            this.locator = locator;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void fromJSON(JSONObject o) {
            this.setId((Long)o.get("id"));
            this.setLocator(Locator.fromLocator((String)o.get("locator")));
            this.setStatus((String)o.get("status"));
        }

        public Build build() {
            return new Build(id, locator, status);
        }
    }
}
