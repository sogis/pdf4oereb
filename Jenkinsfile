pipeline {
    agent any
    stages {
        stage('Prepare') {
            steps {
                git 'https://github.com/edigonzales/pdf4oereb.git'
            }
        }
        stage('Compile') {
            steps {
                sh './gradlew --no-daemon clean classes'
            }
        }
    }
    post {
        always {
            deleteDir() 
        }
    }
}