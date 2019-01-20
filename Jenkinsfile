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
                //echo 'foo bar bar foo'
                sh './gradlew --no-daemon fubar'                
            }
        }  
        stage('Publish') {
            steps {
                sh './gradlew --no-daemon web-service:bootJar'                
                sh './gradlew --no-daemon web-service:buildDockerImage'                
            }
        }               
    }
    post {
        always {
            deleteDir() 
        }
    }
}