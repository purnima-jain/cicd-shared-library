#!/usr/bin/env groovy
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

        stages {
            stage("Initialize Pipeline") {
                steps {
                    ColorStep.green("Initialize Pipeline has started......")
                    echo "Initialize Pipeline has started......"
                }
            }
        }
    }
}