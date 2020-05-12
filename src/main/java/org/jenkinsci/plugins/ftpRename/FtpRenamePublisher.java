/**
 * @author Bruno Cardoso Cantisano
 */
package org.jenkinsci.plugins.ftpRename;
import com.google.common.base.Strings;
import hudson.AbortException;
import hudson.Launcher;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Builder;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.util.FormValidation;
import hudson.util.Secret;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import java.io.IOException;
/**
 * Sample {@link Builder}.
 *
 * <p>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked
 * and a new {@link FtpRenamePublisher} is created. The created
 * instance is persisted to the project configuration XML by using
 * XStream, so this allows you to use instance fields
 * to remember the configuration.
 *
 * <p>
 * When a build is performed, the {@link #perform} method will be invoked. 
 *
 */
public class FtpRenamePublisher extends Notifier implements SimpleBuildStep {
    private String artifactName = "";
    private String newArtifactName = "";
    private String remoteDirectory = "";
    private FtpHandler ftpHandler = null;
    private GlobalEnvironment globalEnvironment = null;
	/**
     * Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
     *
     * @param ftpServer serverhost
	 * @param ftpPort server port
     * @param ftpUser username
     * @param ftpPassword password
     * @param ftpPath artifact path filename
     */

    @DataBoundConstructor
    public FtpRenamePublisher(String ftpServer, int ftpPort, String ftpUser, Secret ftpPassword, String ftpPath) {
		getDescriptor().setFtpServer(ftpServer);
		getDescriptor().setFtpPort(ftpPort);
		getDescriptor().setFtpUser(ftpUser);
		getDescriptor().setFtpPassword(ftpPassword);
		getDescriptor().setFtpPath(ftpPath);
		ftpHandler = new FtpHandler();
		globalEnvironment = new GlobalEnvironment();
    }

    public String getArtifactName() {
        return artifactName;
    }

    @DataBoundSetter
    public void setArtifactName(String artifactName) {
        this.artifactName = artifactName;
    }

    public String getNewArtifactName() {
        return this.newArtifactName;
    }
    @DataBoundSetter
    public void setNewArtifactName(String newArtifactName) {
        this.newArtifactName = newArtifactName;
    }

    @DataBoundSetter
    public void setRemoteDirectory(String remoteDirectory) {
        this.remoteDirectory = remoteDirectory;
    }

    public String getRemoteDirectory() {
        return this.remoteDirectory;
    }

    @Override
    public void perform(Run<?,?> build, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {

        if(Strings.isNullOrEmpty(getArtifactName())){
            throw new AbortException("nice try, but we need the artifact filename");
        }

        if(Strings.isNullOrEmpty(getNewArtifactName())){
            throw new AbortException("nice try, but we need the new artifact filename");
        }

        this.artifactName = globalEnvironment.replaceGlobalVars(getArtifactName(), build.getCharacteristicEnvVars());
        this.newArtifactName = globalEnvironment.replaceGlobalVars(getNewArtifactName(), build.getCharacteristicEnvVars());
		this.remoteDirectory = globalEnvironment.replaceGlobalVars(getRemoteDirectory(), build.getCharacteristicEnvVars());
        //remoteDirectory is optional, so is not checked here
		if(ftpHandler.openConnection(this.getDescriptor().getFtpServer(), 
				this.getDescriptor().getFtpPort(),
				this.getDescriptor().getFtpUser(),
				this.getDescriptor().getFtpPassword(),
				this.getDescriptor().isPassiveMode())){
			String path = this.getDescriptor().getFtpPath() + remoteDirectory + "/";
			if(ftpHandler.changeDirectory(path)){
				if(ftpHandler.renameFtpFile(artifactName, newArtifactName, path)){
					if(!ftpHandler.closeConnection()){
						throw new AbortException("Can't close the connection");
					}
				}else{
					throw new AbortException("Can't rename file");
				}
			}else{
				throw new AbortException("Can't change to directory:" + path);
			}
		}else{
			throw new AbortException("Can't open the connection");
		}
    }

    // Overridden for better buildType safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    /**
     * Descriptor for {@link FtpRenamePublisher}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     *
     * <p>
     * See <tt>src/main/resources/hudson/plugins/hello_world/FtpRenamePublisher/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {


        /**
         * To persist global configuration information,
         * simply store it in a field and call save().
         *
         * <p>
         * If you don't want fields to be persisted, use <tt>transient</tt>.
         */        
        private String ftpServer;
        private int ftpPort;
        private String ftpUser;
        private Secret ftpPassword;	
        private String ftpPath;
        private boolean passiveMode;

        /**
         * In order to load the persisted global configuration, you have to 
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }

        public FormValidation doCheckProjectFile(@QueryParameter String value) {
            if(Strings.isNullOrEmpty(value)){
                return FormValidation.error("value is empty");
            } else{
                return FormValidation.ok();
            }
        }

        public boolean isApplicable(@SuppressWarnings("rawtypes") Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types 
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "FTP Rename";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {        	
        	this.ftpServer = formData.getString("ftpServer");
        	this.ftpPort = formData.getInt("ftpPort");
        	this.ftpUser = formData.getString("ftpUser");
        	this.ftpPassword = Secret.fromString(formData.getString("ftpPassword"));
        	this.ftpPath = formData.getString("ftpPath");
        	this.passiveMode = formData.getBoolean("passiveMode");
            save();
            return super.configure(req,formData);
        }

    	public String getFtpServer() {
            return this.ftpServer;
        }
    	public void setFtpServer(String ftpServer) {
            this.ftpServer = ftpServer;
        }

    	public int getFtpPort() {
    		if (this.ftpPort==0){
    			this.ftpPort = 21;
    		}
            return this.ftpPort;
        }
    	public void setFtpPort(int ftpPort) {
            this.ftpPort = ftpPort;
        }

    	public String getFtpUser() {
            return this.ftpUser;
        }
    	public void setFtpUser(String ftpUser) {
            this.ftpUser = ftpUser;
        }

    	public String getFtpPassword() {
            return Secret.toString(this.ftpPassword);
        }
    	public void setFtpPassword(Secret ftpPassword) {
    		this.ftpPassword = ftpPassword;
        }

        public String getFtpPath() {
            return this.ftpPath;
        }
        public void setFtpPath(String ftpPath) {
            this.ftpPath = ftpPath;
        }

    	public boolean isPassiveMode() {
    		return this.passiveMode;
    	}
    	public void setPassiveMode(boolean passiveMode) {
    		this.passiveMode = passiveMode;
    	}
    }

	@Override
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}
}