#!/usr/bin/env groovy

def call(Map stepParams = [:]) {

    def mandatoryParams = ['gitUrl', 'githubCredentialsId', 'branch'].find { !stepParams."${it}" }
    echo "mandatoryParams :: ${mandatoryParams}" // mandatoryParams :: null

    if(mandatoryParams) {
        throw new Exception("[ERROR] Missing parameter: ${mandatoryParams} is required")
    }

    def branch = stepParams.branch
    echo "branch: ${branch}" // branch: master

    def githubCredentialsId = stepParams.githubCredentialsId
    echo "githubCredentialsId: ${githubCredentialsId}" // githubCredentialsId: GITHUB_CREDENTIAL_ID

    def gitUrl = stepParams.gitUrl
    echo "gitUrl: ${gitUrl}" // gitUrl: https://github.com/purnima-jain/business-application-payments-daily.git
    def name = gitUrl.replaceFirst(/^.*\/([^\/]+).git$/, '$1')
    echo "name: ${name}" // name: business-application-payments-daily

    def domain = evaluateDomain(name)
    echo "domain: ${domain}" // domain: payments

    def domainCode = getDomainCode(domain)
    echo "domainCode: ${domainCode}" // domainCode: PYMT

    def devopsMetadataRepo = stepParams.devopsMetadataRepo ?: "https://github.com/purnima-jain/cicd-metadata.git"
    echo "devopsMetadataRepo: ${devopsMetadataRepo}" // devopsMetadataRepo: https://github.com/purnima-jain/cicd-metadata.git

    def devopsMetadataBranch = stepParams.devopsMetadataBranch ?: "master"
    echo "devopsMetadataBranch: ${devopsMetadataBranch}" // devopsMetadataBranch: master

    git branch : branch, credentialsId : githubCredentialsId, url : gitUrl // Fetches code from the repo
    // git branch : branch, url : gitUrl // Fetches code from the repo
    sh "ls -l"

    try {
        jdkVersion = sh (script: """ cat pom.xml | grep -oP '(?<=<java.version>).*?(?=</java.version>)'""", returnStdout: true).trim()
        echo "jdkVersion: " + jdkVersion // jdkVersion: 21
    } catch(err) {
        echo ("Java version not specified progressing with default java version ${err}")
        withCredentials([usernamePassword(credentialsId: githubCredentialsId, passwordVariable: 'GIT_SERVICEACC_TOKEN', usernameVariable: 'GIT_SERVICEACC_USERNAME')]) {
            echo "GIT_SERVICEACC_TOKEN: ${GIT_SERVICEACC_TOKEN}"       // GIT_SERVICEACC_TOKEN: ****
            echo "GIT_SERVICEACC_USERNAME: ${GIT_SERVICEACC_USERNAME}" // GIT_SERVICEACC_USERNAME: purnima-jain
            
            try {
                configFolderName = devopsMetadataRepo.replaceFirst(/^.*\/([^\/]+).git$/, '$1')
                echo "configFolderName: ${configFolderName}" // configFolderName: cicd-metadata

                url = devopsMetadataRepo.split('//')[1]
                echo "url: ${url}" // url: github.com/purnima-jain/cicd-metadata.git

                metadataDir = url.split('/')[2] 
                echo "metadataDir: ${metadataDir}" // metadataDir: cicd-metadata.git

                metadataDir = metadataDir.split('.git')[0]
                echo "metadataDir: ${metadataDir}" // metadataDir: cicd-metadata

                sh """
                rm -rf ${metadataDir}
                git clone https://${GIT_SERVICEACC_USERNAME}:${GIT_SERVICEACC_TOKEN}@${url} -b ${devopsMetadataBranch}
                ls -l
                cat pom.xml
                """
                echo "Picking Java Version from Parent pom"

                parentArtifactId = readMavenPom file: 'pom.xml'
                parentArtifactId = parentArtifactId.parent.artifactId
                echo "parentArtifactId: ${parentArtifactId}" // parentArtifactId: spring-boot-starter-parent

                domainYaml = readYaml file: "${metadataDir}/domain-config/${domain}-pipeline.yaml"
                gitOrgHost = domainYaml.gitOrgHost
                echo "gitOrgHost: ${gitOrgHost}" // gitOrgHost: github.com/purnima-jain/
                jdkVersion = domainYaml.jdkVersion
                echo "jdkVersion: ${jdkVersion}" // jdkVersion: 
            } catch(error) {
                echo ("Java version not specified in parent pom... progressing with default Java version ${error}")
            }
        }        
    }
    
    return jdkVersion
}