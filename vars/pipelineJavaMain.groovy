import com.purnima.jain.ColorStep

def call(Map pipelineCfg = [:]) {
    def devopsMetadataRepo = pipelineCfg.devopsMetadataRepo
    def devopsMetadataBranch = pipelineCfg.devopsMetadataBranch
    def helmChartGitRepo = pipelineCfg.helmChartGitRepo
    def helmChartVersion = pipelineCfg.helmChartVersion
    def nexusCredId = pipelineCfg.nexusCredId
    def githubCredentialsId = pipelineCfg.githubCredentialsId
    def configChange = pipelineCfg.configChange
    def executeSast = pipelineCfg.executeSast

    def ColorStep = new ColorStep()

    pipeline {
        agent any

        // AnsiColor at pipeline level
        options {
            ansiColor('xterm')
        }

        stages {
            stage("Initialize Pipeline") {
                steps {
                    script {
                        ColorStep.green("Initialize Pipeline has started......")
                    }                    
                    echo "Initialize Pipeline has started......"
                }
            }
        }
    }
}