#!/usr/bin/env groovy

import com.purnima.jain.MergeConfig

def call(Map stepParams = [:]) {

    def configFolderName = stepParams.configFolderName
    echo "configFolderName: ${configFolderName}" // configFolderName: cicd-metadata

    echo "Get appConfig properties \n\t WORKSPACE: ${env.WORKSPACE}\n\t name: ${env.name}\n\t domain: ${env.domain}"
    def temp = readYaml(file: "${configFolderName}/domain-config/${env.domain}-pipeline.yaml")
    echo "temp: ${temp}" // temp: [gitOrgHost:github.com/purnima-jain/, jdkVersion:17.....]

    sh """ rm -rf application.yaml """
    writeYaml file: "application.yaml", data: temp
    sh """ sed -i "s/reponame/${name}/g" application.yaml """

    def pipelineConfig = readYaml(file: 'application.yaml')
    echo "pipelineConfig: ${pipelineConfig}" 
    
    def globalConfig = readYaml(file: "${configFolderName}/global.yaml")
    echo "globalConfig: ${globalConfig}"

    def toolingConfig = readYaml(file: "${configFolderName}/tooling.yaml")
    echo "toolingConfig: ${toolingConfig}"

    def appConfig = MergeConfig.joinFiles(globalConfig, pipelineConfig)
    echo "appConfig: ${appConfig}"
    echo "appConfig.gitOrgHost: ${appConfig.gitOrgHost}"

    return "Crap...Crap...Crap..."
}