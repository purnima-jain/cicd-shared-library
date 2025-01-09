import jenkins.model.Jenkins;

pluginManager = Jenkins.instance.pluginManager
updateCenter = Jenkins.instance.updateCenter

// Call Plugin Catalog and Download all the information that is required
pluginManager.doCheckUpdatesServer()

// List of Plugins with Dependencies
["pipeline-stage-view", "ansicolor"].each {
    if(! pluginManager.getPlugin(it)) {
        deployment = updateCenter.getPlugin(it).deploy(true)
        deployment.get() 
    }
} 

// Restart Jenkins after installing plugins (optional)
Jenkins.instance.restart()
