#!/usr/bin/env groovy

def call(Map stepParams = [:]) {

    def mandatoryParams = ['gitUrl', 'githubCredentialsId', 'branch'].find { !stepParams."${it}" }
    echo "mandatoryParams :: ${mandatoryParams}" // mandatoryParams :: branch

    
    def jdkVersion = "1.17"
    return jdkVersion
}