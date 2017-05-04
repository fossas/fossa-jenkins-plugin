package io.fossa.plugins.jenkins;
import hudson.EnvVars;
import hudson.Launcher;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import io.fossa.service.Build;
import io.fossa.service.FossaService;
import io.fossa.service.Locator;
import io.fossa.service.Revision;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;

public class FossaAnalysisBuildStep extends Builder implements SimpleBuildStep {
    private String baseURL;

    @DataBoundConstructor
    public FossaAnalysisBuildStep(String baseURL) {
        this.baseURL = baseURL;
    }

    @Override
    public void perform(Run<?,?> build, FilePath workspace, Launcher launcher, TaskListener listener) throws IOException, InterruptedException {
        EnvVars envVars = build.getEnvironment(listener);
        String commitId = envVars.get("GIT_COMMIT",null);
        String branch = envVars.get("GIT_COMMIT",null);
        String gitUrl = envVars.get("GIT_URL", null);

        if (commitId == null || branch == null || gitUrl == null) {
            listener.fatalError("The Fossa Plugin must be used in conjunction with Git.");
            return;
        }

        FossaService service = new FossaService(baseURL, listener.getLogger());

        Build fbuild;
        try {
            // Wait 1 hour
            fbuild = service.analyzeAndWaitUntilIsFinished(new Locator("git", gitUrl, commitId), 10000, 360);
        } catch (ParseException e) {
            listener.fatalError(e.getMessage());
            return;
        }

        if (!fbuild.isFinished()) {
            listener.fatalError("Fossa could not finish analyzing in the configured time.");
            return;
        }

        Revision revision;
        try {
            // Wait 5 minutes
            revision = service.waitUntilScanIsFinished(service.getRevision(fbuild.getLocator()), 10000, 30);
        } catch (ParseException e) {
            listener.fatalError(e.getMessage());
            return;
        }

        if (!revision.isFinishedScanning()) {
            listener.fatalError("Fossa could not finish scanning in the configured time.");
            return;
        }

        if (revision.getUnresolvedIssueCount() > 0) {
            listener.fatalError(String.format("Fossa found %s issues.", revision.getUnresolvedIssueCount()));
        }
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        public DescriptorImpl() {
            load();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        public String getDisplayName() {
            return "Analyze in Fossa";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            save();
            return super.configure(req,formData);
        }
    }
}

