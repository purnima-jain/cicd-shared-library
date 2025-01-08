#!/usr/bin/env groovy

// import com.wf.MergeConfig

def call(Map stepParams = [:]) {

    echo "Get appConfig properties \n\t WORKSPACE: ${env.WORKSPACE}\n\t name: ${env.name}\n\t domain: ${env.domain}"
    def temp = readYaml(file: "${configFolderName}/domain-config/${env.domain}-pipeline.yaml")
    echo "temp: ${temp}"

    return "Crap...Crap...Crap..."
}