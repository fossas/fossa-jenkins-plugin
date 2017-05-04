package io.fossa.service;

public class Locator {
    private String fetcher;
    private String pkg;
    private String revision;

    public Locator(String fetcher, String pkg, String revision) {
        this.fetcher = fetcher;
        this.pkg = pkg;
        this.revision = revision;
    }

    public static Locator fromLocator (String locator) {
        int fetcherSeparatorIndex = locator.indexOf("+");
        int revisionSeparatorIndex = locator.indexOf("$");
        if (fetcherSeparatorIndex == -1 || revisionSeparatorIndex == -1) {
            return null;
        }

        String fetcher = locator.substring(0, fetcherSeparatorIndex);
        String pkg = locator.substring(fetcherSeparatorIndex+1, revisionSeparatorIndex);
        String revision = locator.substring(revisionSeparatorIndex+1, locator.length());

        return new Locator(fetcher, pkg, revision);
    }

    public void setFetcher(String fetcher) {
        this.fetcher = fetcher;
    }

    public String getFetcher() {
        return this.fetcher;
    }

    public void setPackage(String pkg) {
        this.pkg = pkg;
    }

    public String getPackage() {
        return this.pkg;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getRevision() {
        return this.revision;
    }

    public String toString() {
        return String.format("%s+%s$%s", this.fetcher, this.pkg, this.revision);
    }
}
