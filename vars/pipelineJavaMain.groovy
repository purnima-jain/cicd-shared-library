import com.purnima.jain.ColorStep

def call(Map pipelineCfg = [:]) {
    
    /* def devopsMetadataRepo = pipelineCfg.devopsMetadataRepo
    def devopsMetadataBranch = pipelineCfg.devopsMetadataBranch
    def helmChartGitRepo = pipelineCfg.helmChartGitRepo
    def helmChartVersion = pipelineCfg.helmChartVersion
    def nexusCredId = pipelineCfg.nexusCredId
    def githubCredentialsId = pipelineCfg.githubCredentialsId  */
    def configChange = pipelineCfg.configChange ?: 'false' // Setting defaults to avoid failure of the first run
    def executeSast = pipelineCfg.executeSast ?: 'true'    // Setting defaults to avoid failure of the first run

    def ColorStep = new ColorStep()

    pipeline {
        agent any

        tools {
            maven "maven.3.8.7"
        }

        // AnsiColor at pipeline level
        options {
            ansiColor('xterm')
        }

        stages {
            stage("Initialize Pipeline") {
                steps {
                    script {
                        ColorStep.green("Initialize Pipeline has started......")
                        echo "configChange: ${configChange}"
                        echo "executeSast: ${executeSast}"
                    }                    
                    echo "Initialize Pipeline has started......"
                }
            }
        }
    }
}