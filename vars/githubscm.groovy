def checkoutIfExists(String repository, String author, String branches, String defaultAuthor, 
    String defaultBranches, boolean mergeTarget = false, 
    def credentials = ['token': 'kie-ci1-token', 'usernamePassword': 'kie-ci']) {
        echo "Inside githubscm -> checkoutIfExists()......"
        echo "credentials: ${credentials}" // credentials: [token:kie-ci1-token, usernamePassword:kie-ci]
        echo "credentials['token']: ${credentials['token']}" // credentials['token']: kie-ci1-token
        echo "credentials['usernamePassword']: ${credentials['usernamePassword']}" // credentials['usernamePassword']: kie-ci

        assert credentials['token']
        assert credentials['usernamePassword']

        def sourceAuthor = author
        def sourceRepository = getForkedProjectName(defaultAuthor, repository, sourceAuthor, credentials['token']) ?: repository
        echo "sourceRepository: ${sourceRepository}" // 

        // Checks source group and branch (for cases where the branch has been created in the author's forked project)
        def repositoryScm = getRepositoryScm(sourceRepository, author, branches, credentials['usernamePassword'])
        echo "repositoryScm: ${repositoryScm}" //

        if(repositoryScm == null) {
            // Checks target group and and source branch (for cases where the branch has been created in the target project itself
            repositoryScm = getRepositoryScm(repository, defaultAuthor, branches, credentials['usernamePassword'])
            sourceAuthor = repositoryScm ? defaultAuthor : author
        }

        if(repositoryScm != null && (!mergeTarget || hasPullRequest(defaultAuthor, repository, author, branches, credentials['token']))) {
            if(mergeTarget) {
                mergeSourceIntoTarget(sourceRepository, sourceAuthor, branches, repository, defaultAuthor, defaultBranches, credentials['usernamePassword'])
            } else {
                checkout repositoryScm
            }
        } else {
            checkout resolveRepository(repository, defaultAuthor, defaultBranches, false, credentials['usernamePassword'])
        }
}

def hasPullRequest(String group, String repository, String author, String branch, String credentialsId = 'kie-ci1-token') {    
    echo "Inside githubscm -> hasPullRequest()......"
    return hasForkPullRequest(group, repository, author, branch, credentialsId) || hasOriginPullRequest(group, repository, branch, credentialsId)
}

def hasForkPullRequest(String group, String repository, String author, String branch, String credentialsId = 'kie-ci1-token') {
    echo "Inside githubscm -> hasForkPullRequest()......"
    def result = false
    withCredentials([string(credentialsId: credentialsId, variable: 'OAUTHTOKEN')]) {
        def curlResult = sh(returnStdout: true, script: "curl --globoff -H \"Authorization: token ${OAUTHTOKEN}\" 'https://api.github.com/repos/${group}/${repository}/pulls?head=${author}:${branch}&state=open'")?.trim()
        if(curlResult) {
            def pullRequestJsonObject = readJSON text: curlResult
            result = pullRequestJsonObject.size() > 0
        }
    }
    println "[INFO] has pull request for ${group}/${repository}:${author}:${branch} -> ${result}"
    return result
}

def hasOriginPullRequest(String group, String repository, String branch, String credentialsId  = 'kie-ci1-token') {
    echo "Inside githubscm -> hasOriginPullRequest()......"
    return hasForkPullRequest(group, repository, group, branch, credentialsId)
}

def getForkedProjectName(String group, String repository, String owner, String credentialsId = 'kie-ci1-token', 
    int page = 1, int perPage = 100, replays = 3) {
        echo "Inside githubscm -> getForkedProjectName()......"
        if(group == owner) {
            echo "Inside githubscm -> getForkedProjectName() -> group == owner ......"
            return repository
        }
        def result = null
        withCredentials([string(credentialsId: credentialsId, variable: 'OAUTHTOKEN')]) {
            def forkedProjects = null

            // URL: https://api.github.com/repos/purnima-jain/cicd-shared-library/forks?per_page=10&page=10
            def curlResult = sh(returnStdout: true, script: "curl -H \"Authorization: token ${OAUTHTOKEN}\" 'https://api.github.com/repos/${group}/${repository}/forks?per_page=${perPage}&page=${page}'")?.trim()
            if(curlResult) {
                forkedProjects = readJSON text: curlResult
            }
            if (result == null && forkedProjects != null && forkedProjects.size() > 0) {
                try {
                    def forkedProject = forkedProjects.find { it.owner.login == owner }
                    result = forkedProject ? forkedProject.name : getForkedProjectName(group, repository, owner, credentialsId, ++page, perPage) 
                } catch (MissingPropertyException e) {
                    if(--replays <= 0) {
                        throw new Exception("Error getting forked project name for $group/$repository/forks?per_page=${perPage}&page=${page}. Communication error, please relaunch job.") 
                    } else {
                        println("[ERROR] Getting forked project name for $group/$repository/forks?per_page=${perPage}&page=${page}. Replaying... [${replays}]")
                        result = getForkedProjectName(group, repository, owner, credentialsId, page, perPage, replays)
                    }
                }
            }
        }
        return result
}

def getRepositoryScm(String repository, String author, String branches, String credentialsId = 'kie-ci') {
    echo "Inside githubscm -> getRepositoryScm()......"
    def repositoryScm = resolveRepository(repository, author, branches, true, credentialsId)
    def tempDir = sh(script: 'mktemp -d', returnStdout: true).trim() // Create a temp directory
    dir (tempDir) {
        try {
            checkout repositoryScm
        } catch(Exception ex) {
            println "[WARNING] Branches [${branches}] from repository ${repository} not found in ${author} organization."
            repositoryScm = null
        }
    }
    return repositoryScm
}

def resolveRepository(String repository, String author, String branches, boolean ignoreErrors, String credentialID = 'kie-ci') {
    echo "Inside githubscm -> resolveRepository()......"
    println "[INFO] Resolving Repository https://github.com/${author}/${repository}:${branches}. CredentialsID: ${credentialID}"
    return [$class                           : 'GitSCM',
            branches                         : [[name: branches]],
            doGenerateSubmoduleConfigurations: false,
            extensions                       : [[$class: 'CleanBeforeCheckout'],
                                                [$class             : 'SubmoduleOption',
                                                 disableSubmodules  : false,
                                                 parentCredentials  : true,
                                                 recursiveSubmodules: true,
                                                 reference          : '',
                                                 trackingSubmodules : false],
                                                [$class           : 'RelativeTargetDirectory',
                                                 relativeTargetDir: './']],
            submoduleCfg                     : [],
            userRemoteConfigs                : [[credentialsId: credentialID, url: "https://github.com/${author}/${repository}.git"]]
    ] 
}

def mergeSourceIntoTarget(String sourceRepository, String sourceAuthor, String sourceBranches, 
    String targetRepository, String targetAuthor, String targetbranches, String credentialsId = 'kie-ci') {
    echo "Inside githubscm -> mergeSourceIntoTarget()......"
    println "[INFO] Merging source [${sourceAuthor}/${sourceRepository}:${sourceBranches}] into target [${targetAuthor}/${targetRepository}:${targetbranches}..]"
    checkout resolveRepository(targetRepository, targetAuthor, targetbranches, false, credentialsId)
    setUserConfigFromCreds(credentialsId)
    def targetCommit = getCommit()

    try {
        withCredentials([usernameColonPassword(credentialsId: credentialsId, variable: 'kieCiUserPassword')]) {
            sh "git pull https://${kieCiUserPassword}@github.com/${sourceAuthor}/${sourceRepository} ${sourceBranches}"
        }
    } catch (Exception e) {
        println """
        -------------------------------------------------------------
        [ERROR] Can't merge source into Target. Please rebase PR branch.
        -------------------------------------------------------------
        Source: git://github.com/${sourceAuthor}/${sourceRepository} ${sourceBranches}
        Target: ${targetCommit}
        -------------------------------------------------------------
        """
        throw e
    }
    def mergedCommit = getCommit()

    println """
    -------------------------------------------------------------
    [INFO] Source merged into Target
    -------------------------------------------------------------
    Target: ${targetCommit}
    Produced: ${mergedCommit}
    -------------------------------------------------------------
    """
}

def setUserConfigFromCreds(String credentialsId = 'kie-ci') {
    echo "Inside githubscm -> setUserConfigFromCreds()......"
    withCredentials([usernamePassword(credentialsId: "${credentialsId}", usernameVariable: 'GITHUB_USER', passwordVariable = 'GITHUB_TOKEN')]) {
        setUserConfig("${GITHUB_USER}")
    }
}

def setUserConfig(String username) {
    echo "Inside githubscm -> setUserConfig()......"
    sh "git config user.email ${username}@gmail.com"
    sh "git config user.name ${username}"
}

def getCommit() {
    echo "Inside githubscm -> getCommit()......"
    // Retrieves the latest commit from the Git log in a concise, single-line format.
    return sh(returnStdout: true, script: 'git log --oneline -1').trim() 
}