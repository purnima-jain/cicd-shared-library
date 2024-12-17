def getBuildContext(Map config, String architecture) {
  if (architecture=='windows-amd64') {
    return config.windowsContext
  }
  return config.linuxContext
}

def buildAndPush() {
    echo "Build and Push ${REPO_NAME} on ${BUILD_CONTEXT}....."
}

def call(Map config) {
    pipeline {
        agent any
        environment {
            REPO_NAME = "${config.repoName}"
        }
        stages {
            stage('windows-amd64') {
                environment {
                    BUILD_CONTEXT = getBuildContext(config, env.STAGE_NAME)
                }                   
                steps {
                    script{
                        buildAndPush()
                    }
                }
            }
            stage('notify') {
                steps{
                    echo "https://hub.docker.com/r/$REPO_NAME"
                }
            }
        }
    }
}