import jenkins.model.*;
import hudson.util.Secret;
import hudson.security.*;

println "[SECURITY] Configuring LDAP Security..."
jenkins = Jenkins.instance

jenkins.securityRealm = new HudsonPrivateSecurityRealm(false,false,null)
jenkins.securityRealm.createAccount('developer','developer')
jenkins.securityRealm.createAccount('administrator','administrator')

println "[SECURITY] Configuring Project Matrix Security..."

class BuildPermission {
	static buildNewAccessList(userOrGroup, permissions) {
    	def newPermissionsMap = [:]
    	permissions.each {
      		newPermissionsMap.put(Permission.fromId(it), userOrGroup)
    	}
    	newPermissionsMap
	}
}

strategy = new ProjectMatrixAuthorizationStrategy()
jenkins.authorizationStrategy = strategy

administratorPermissions = [
    "hudson.model.Hudson.Administer"
]

authenticatedPermissions = [
    "com.cloudbees.plugins.credentials.CredentialsProvider.View",
    "com.sonyericsson.hudson.plugins.gerrit.trigger.PluginImpl.ManualTrigger",
    "com.sonyericsson.hudson.plugins.gerrit.trigger.PluginImpl.Retrigger",
    "hudson.model.Hudson.Read",
    "hudson.model.Item.Build",
    "hudson.model.Item.Cancel",
    "hudson.model.Item.Discover",
    "hudson.model.Item.Read",
    "hudson.model.Item.Release",
    "hudson.model.Item.Workspace",
    "hudson.model.Run.Delete",
    "hudson.model.Run.Update",
    "hudson.model.View.Configure",
    "hudson.model.View.Create",
    "hudson.model.View.Delete",
    "hudson.model.View.Read"
]

anonymousPermissions = [
    "hudson.model.Hudson.Read",
    "hudson.model.Item.Discover"
]

BuildPermission.buildNewAccessList("administrator", administratorPermissions).each { p, u -> strategy.add(p, u) }
BuildPermission.buildNewAccessList("developer", authenticatedPermissions).each { p, u -> strategy.add(p, u) }
BuildPermission.buildNewAccessList("anonymous", anonymousPermissions).each { p, u -> strategy.add(p, u) }

println "[SECURITY] Security... Done"
