pipeline {
    agent any
    
    environment {
        // Maven Configuration
        MAVEN_HOME = tool 'Maven-3.9'
        MAVEN_OPTS = '-Xmx1024m'
        
        // Application Configuration
        APP_NAME = 'test-device-api'
        APP_VERSION = '1.0.0'
        
        // MongoDB Configuration
        MONGODB_URI = credentials('mongodb-uri')
    }
    
    tools {
        maven 'Maven-3.9'
        jdk 'JDK-17'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out code from repository...'
                checkout scm
                script {
                    env.GIT_COMMIT_MSG = bat(script: '@git log -1 --pretty=%%B', returnStdout: true).trim()
                    env.GIT_AUTHOR = bat(script: '@git log -1 --pretty=%%an', returnStdout: true).trim()
                }
                echo "Commit: ${env.GIT_COMMIT_MSG} by ${env.GIT_AUTHOR}"
            }
        }
        
        stage('Build') {
            steps {
                echo 'Building the application with Maven...'
                bat 'mvn clean compile -DskipTests'
            }
        }
        
        stage('Unit Tests') {
            steps {
                echo 'Running unit tests...'
                bat 'mvn test'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Code Quality Analysis') {
            steps {
                echo 'Running code quality checks...'
                bat 'mvn checkstyle:checkstyle pmd:pmd'
            }
        }
        
        stage('Package') {
            steps {
                echo 'Packaging the application...'
                bat 'mvn package -DskipTests spring-boot:repackage'
            }
            post {
                success {
                    archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
                }
            }
        }
    }
    
    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
