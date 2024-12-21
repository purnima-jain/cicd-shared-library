import com.purnima.jain.*

def call() {
    pipeline {
        agent any
        environment {
            MODULE='m4'
        }
        stages {
            stage('Verify') {                  
                steps {
                    echo "Module: ${MODULE}"
                    sh 'git version'
                    def emailsInstance = new Emails();
                    echo "First Name: ${emailsInstance.firstName}"
                }
            }
        }
    }
}