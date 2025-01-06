import com.purnima.jain.ColorStep

def call(Map pipelineCfg = [:]) {
    
    /* def devopsMetadataRepo = pipelineCfg.devopsMetadataRepo */
    def devopsMetadataBranch = pipelineCfg.devopsMetadataBranch
    /* def helmChartGitRepo = pipelineCfg.helmChartGitRepo
    def helmChartVersion = pipelineCfg.helmChartVersion
    def nexusCredId = pipelineCfg.nexusCredId  */
    def githubCredentialsId = pipelineCfg.githubCredentialsId
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
                        echo "configChange: " + configChange // configChange: false
                        echo "executeSast: " + executeSast   // executeSast: false

                        echo "gitUrl: " + env.GIT_URL              // gitUrl: https://github.com/purnima-jain/cicd-pipelines.git
                        echo "executeSast: " + githubCredentialsId // executeSast: GITHUB_CREDENTIAL_ID
                        echo "env.BRANCH_NAME: " + env.BRANCH_NAME // env.BRANCH_NAME: null Expl: This variable only works in a multibranch pipline
                        echo "pipelineCfg.devopsMetadataBranch: " + pipelineCfg.devopsMetadataBranch // pipelineCfg.devopsMetadataBranch: master

                        def jdkVersion = getPipelineTools(
                            gitUrl: "https://github.com/purnima-jain/business-application-payments-daily.git" // env.GIT_URL, Hardcoding it temporarily
                            githubCredentialsId: githubCredentialsId,
                            branch: "master",               // Hardcoding it temporarily env.BRANCH_NAME,
                            devopsMetadataBranch: pipelineCfg.devopsMetadataBranch
                        )
                        echo "jdkVersion: " + jdkVersion

                        
                    }                    
                    echo "Initialize Pipeline has started......"
                }
            }
        }
    }
}