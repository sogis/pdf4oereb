pipeline {
    agent any
    environment {
        dockerRegistryUser = credentials('dockerRegistryUser')
        dockerRegistryPass = credentials('dockerRegistryPass')
    }    
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
        stage('Test') {
            steps {
                //sh './gradlew --no-daemon library:test library:wmsTest web-service:test'
            }
        }  
        stage('Publish') {
            steps {
                sh './gradlew --no-daemon library:build library:distZip -x library:test -x library:wmsTest'                
                archiveArtifacts artifacts: 'library/build/distributions/pdf4oereb-1.0.*.zip', fingerprint: true
                //sh './gradlew --no-daemon web-service:bootJar'                
                //sh './gradlew --no-daemon web-service:pushDockerImages'                
            }
        }               
    }
    post {
        always {
            deleteDir() 
        }
    }
}