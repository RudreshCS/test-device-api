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
                echo 'Code checkout completed'
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
        
        stage('Build Docker Image') {
            steps {
                echo 'Building Docker image...'
                bat "docker build -t ${APP_NAME}:${BUILD_NUMBER} ."
                bat "docker tag ${APP_NAME}:${BUILD_NUMBER} ${APP_NAME}:latest"
            }
        }
        
        stage('Run Docker Container') {
            steps {
                echo 'Running Docker container...'
                script {
                    bat """
                        docker stop ${APP_NAME} 2>nul || echo Container not running
                        docker rm ${APP_NAME} 2>nul || echo Container does not exist
                        docker run -d --name ${APP_NAME} -p 8080:8080 -e "MONGODB_URI=%MONGODB_URI%" ${APP_NAME}:latest
                    """
                }
                echo 'Application running on http://localhost:8080'
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
