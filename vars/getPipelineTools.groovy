#!/usr/bin/env groovy

def call(Map stepParams = [:]) {

    def mandatoryParams = ['gitUrl', 'githubCredentialsId', 'branch'].find { !stepParams."${it}" }
    echo "mandatoryParams :: ${mandatoryParams}" // mandatoryParams :: null

    if(mandatoryParams) {
        throw new Exception("[ERROR] Missing parameter: ${mandatoryParams} is required")
    }

    def gitUrl = stepParams.gitUrl
    echo "gitUrl: ${gitUrl}" // gitUrl: https://github.com/purnima-jain/business-application-payments-daily.git
    def name = gitUrl.replaceFirst(/^.*\/([^\/]+).git$/, '$1')
    echo "name: ${name}" // name: business-application-payments-daily

    def domain = evaluateDomain(name)
    echo "domain: ${domain}" // domain: payments

    def domainCode = getDomainCode(domain)
    echo "domainCode: ${domainCode}" // domainCode: PYMT

    // git branch : stepParams.branch, credentialsId : stepParams.githubCredentialsId, url : stepParams.gitUrl
    git branch : stepParams.branch, url : stepParams.gitUrl

    sh "ls -l"

    
    def jdkVersion = "1.17"
    return jdkVersion
}