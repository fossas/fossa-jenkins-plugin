package io.fossa.service;

import org.json.simple.JSONObject;

public class Build {
    private int id;
    private Locator locator;
    private String status;

    public Build(Integer id, Locator locator, String status) {
        this.id = id;
        this.locator = locator;
        this.status = status;
    }

    public Locator getLocator() {
        return locator;
    }

    public void setLocator(Locator locator) {
        this.locator = locator;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isFinished() {
        return this.status.equals("SUCCEEDED") || this.status.equals("FAILED");
    }

    public static class Builder {
        private int id;
        private Locator locator;
        private String status;

        public Builder() {}

        public void setId(int id) {
            this.id = id;
        }

        public void setLocator(Locator locator) {
            this.locator = locator;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void fromJSON(JSONObject o) {
            this.setId((Integer)o.get("id"));
            this.setLocator(Locator.fromLocator((String)o.get("locator")));
            this.setStatus((String)o.get("status"));
        }

        public Build build() {
            return new Build(id, locator, status);
        }
    }
}
