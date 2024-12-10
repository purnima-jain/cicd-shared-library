def call() {
    node {
        sh '''
            git version
            java --version       
        '''
    }
}