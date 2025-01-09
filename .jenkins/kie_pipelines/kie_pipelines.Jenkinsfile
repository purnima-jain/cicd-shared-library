library identifier: 'cicd-shared-library@master', 
      retriever: modernSCM([$class: 'GitSCMSource', 
                           remote: 'https://github.com/purnima-jain/cicd-shared-library.git'])


// Hard-Coding Environment Variables - START
// env.CHANGE_AUTHOR = "Purnima Jain"
// env.CHANGE_BRANCH = "master"
// env.CHANGE_TARGET = "master"
env.ghprbAuthorRepoGitUrl = "https://github.com/purnima-jain/cicd-shared-library.git"
// Hard-Coding Environment Variables - END

changeAuthor = getGroup() ?: env.CHANGE_AUTHOR
echo "changeAuthor: ${changeAuthor}" // changeAuthor: purnima-jain

pipeline {

    agent any

    stages {

        stage ('Initialize') {
            echo "Inside Stage Initialize......"
        }
    }
}


def getGroup() {
    def group = getProjectGroupName(getProject())[0]
    echo "*group: ${group}" // *group: purnima-jain
    return group
}

def getProject() {
    // The =~ operator is used for pattern matching with regular expressions. 
    // Returns a Matcher object if there's a match, or false if there isn't.
    // The =~ operator in Groovy performs a regex match and returns a list of matches. 
    // The [0][8] part accesses the first match (the entire URL) and then the eighth capture group, 
    //          which corresponds to the repository path (username and repository name).
    def project = (ghprbAuthorRepoGitUrl =~ /((git|ssh|http(s)?)|(git@[\w\.]+))(:(\/\/)?(github.com\/))([\w\.@\:\/\-~]+)(\.git)(\/)?/)[0][8]
    echo "*project: ${project}" // *project: purnima-jain/cicd-shared-library
    return project
}

def getProjectGroupName(String project, String defaultGroup = "purnima-jain") {
    def projectNameGroup = project.split("\\/")
    echo "projectNameGroup: ${projectNameGroup}" // projectNameGroup: [purnima-jain, cicd-shared-library]

    def group = projectNameGroup.size() > 1 ? projectNameGroup[0] : defaultGroup
    echo "**group: ${group}" // **group: purnima-jain

    def name = projectNameGroup.size() > 1 ? projectNameGroup[1] : project
    echo "name: ${name}" // name: cicd-shared-library

    return [group, name]
}