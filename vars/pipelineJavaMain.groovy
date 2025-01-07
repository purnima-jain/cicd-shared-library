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
            jdk getPipelineTools(
                            gitUrl: "https://github.com/purnima-jain/business-application-payments-daily.git", // env.GIT_URL, Hardcoding it temporarily
                            githubCredentialsId: githubCredentialsId, // githubCredentialsId: GITHUB_CREDENTIAL_ID
                            branch: "master",               // Hardcoding it temporarily env.BRANCH_NAME,
                            devopsMetadataBranch: pipelineCfg.devopsMetadataBranch // pipelineCfg.devopsMetadataBranch: master
                        )
        }
        
        options {
            ansiColor('xterm') // AnsiColor at pipeline level
            timestamps()
            disableConcurrentBuilds()
            timeout(time: 30, unit: 'MINUTES')
            buildDiscarder(logRotator(
                numToKeepStr: '15',
                daysToKeepStr: '-1',
                artifactNumToKeepStr: '-1',
                artifactDaysToKeepStr: '-1'
            ))
        }

        environment {
            BASEWORKPATH = "${pwd()}/work"

            def gitUrl = "https://github.com/purnima-jain/business-application-payments-daily.git" // env.GIT_URL but hard-coding it temporarily
            echo "gitUrl: ${gitUrl}" // gitUrl: https://github.com/purnima-jain/business-application-payments-daily.git

            def name = gitUrl.replaceFirst(/^.*\/([^\/]+).git$/, '$1')
            echo "name: ${name}" // name: business-application-payments-daily

            def domain = evaluateDomain(name)
            echo "domain: ${domain}" // domain: payments
        }



        stages {
            stage("Clean Workspace") {
                steps {
                    script {
                        sh "ls"
                        cleanWs()
                        sh "ls"
                    }
                }
            }
            stage("Initialize Pipeline") {
                steps {
                    script {

                        echo "configChange: " + configChange // configChange: false
                        echo "executeSast: " + executeSast   // executeSast: false

                        withCredentials([
                            usernamePassword(credentialsId: "${nexusCredId}", usernameVariable: 'NEXUS_SERVICEACC_USERNAME', passwordVariable: 'NEXUS_SERVICEACC_TOKEN'),
                            usernamePassword(credentialsId: "${githubCredentialsId}", usernameVariable: 'GIT_SERVICEACC_USERNAME', passwordVariable: 'GIT_SERVICEACC_TOKEN')
                        ]) {
                            echo "GIT_SERVICEACC_USERNAME: ${GIT_SERVICEACC_USERNAME}"
                            echo "GIT_SERVICEACC_TOKEN: ${GIT_SERVICEACC_TOKEN}"
                            
                            echo "NEXUS_SERVICEACC_USERNAME: ${NEXUS_SERVICEACC_USERNAME}"
                            echo "NEXUS_SERVICEACC_TOKEN: ${NEXUS_SERVICEACC_TOKEN}"

                            echo "gitUrl: " + env.GIT_URL              // gitUrl: https://github.com/purnima-jain/cicd-pipelines.git
                            echo "githubCredentialsId: " + githubCredentialsId // githubCredentialsId: GITHUB_CREDENTIAL_ID
                            echo "env.BRANCH_NAME: " + env.BRANCH_NAME // env.BRANCH_NAME: null Expl: This variable only works in a multibranch pipline
                            echo "pipelineCfg.devopsMetadataBranch: " + pipelineCfg.devopsMetadataBranch // pipelineCfg.devopsMetadataBranch: master
                            def jdkVersion = getPipelineTools(
                                gitUrl: "https://github.com/purnima-jain/business-application-payments-daily.git", // env.GIT_URL, Hardcoding it temporarily
                                githubCredentialsId: githubCredentialsId,
                                branch: "master",               // Hardcoding it temporarily env.BRANCH_NAME,
                                devopsMetadataBranch: pipelineCfg.devopsMetadataBranch
                            )
                            echo "jdkVersion: " + jdkVersion // jdkVersion: 17

                            ColorStep.green("Initialize Stage has started......")
                        }






                        
                        

                        

                        

                        
                    }                    
                    echo "Initialize Pipeline has started......"
                }
            }
        }
    }
}