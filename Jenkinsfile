pipeline {
    agent any
    
    environment {
        // Maven Configuration
        MAVEN_HOME = tool 'Maven'
        MAVEN_OPTS = '-Xmx1024m -XX:MaxPermSize=512m'
        
        // Docker Configuration
        DOCKER_IMAGE = 'test-device-api'
        DOCKER_TAG = "${BUILD_NUMBER}"
        DOCKER_REGISTRY = 'your-docker-registry' // Update with your registry
        DOCKER_CREDENTIALS_ID = 'docker-hub-credentials' // Update with your Jenkins credentials ID
        
        // Application Configuration
        APP_NAME = 'test-device-api'
        APP_VERSION = '1.0.0'
        
        // MongoDB Configuration (for testing/deployment)
        MONGODB_URI = credentials('mongodb-uri') // Store in Jenkins credentials
    }
    
    tools {
        maven 'Maven'
        jdk 'JDK-17'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out code from repository...'
                checkout scm
                script {
                    env.GIT_COMMIT_MSG = sh(script: 'git log -1 --pretty=%B', returnStdout: true).trim()
                    env.GIT_AUTHOR = sh(script: 'git log -1 --pretty=%an', returnStdout: true).trim()
                }
                echo "Commit: ${env.GIT_COMMIT_MSG} by ${env.GIT_AUTHOR}"
            }
        }
        
        stage('Build') {
            steps {
                echo 'Building the application with Maven...'
                sh '''
                    mvn clean compile -DskipTests
                '''
            }
        }
        
        stage('Unit Tests') {
            steps {
                echo 'Running unit tests...'
                sh '''
                    mvn test
                '''
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                    jacoco(
                        execPattern: '**/target/jacoco.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src/main/java'
                    )
                }
            }
        }
        
        stage('Code Quality Analysis') {
            steps {
                echo 'Running code quality checks...'
                sh '''
                    mvn checkstyle:checkstyle pmd:pmd findbugs:findbugs
                '''
            }
            post {
                always {
                    recordIssues(
                        enabledForFailure: true,
                        tools: [
                            checkStyle(pattern: '**/target/checkstyle-result.xml'),
                            pmdParser(pattern: '**/target/pmd.xml')
                        ]
                    )
                }
            }
        }
        
        stage('Package') {
            steps {
                echo 'Packaging the application...'
                sh '''
                    mvn package -DskipTests spring-boot:repackage
                '''
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
                script {
                    docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}")
                    docker.build("${DOCKER_IMAGE}:latest")
                }
            }
        }
        
        stage('Push Docker Image') {
            when {
                branch 'jenkins_integration_main'
            }
            steps {
                echo 'Pushing Docker image to registry...'
                script {
                    docker.withRegistry("https://${DOCKER_REGISTRY}", "${DOCKER_CREDENTIALS_ID}") {
                        docker.image("${DOCKER_IMAGE}:${DOCKER_TAG}").push()
                        docker.image("${DOCKER_IMAGE}:latest").push()
                    }
                }
            }
        }
        
        stage('Security Scan') {
            steps {
                echo 'Scanning Docker image for vulnerabilities...'
                script {
                    sh """
                        docker run --rm -v /var/run/docker.sock:/var/run/docker.sock \
                        aquasec/trivy image --severity HIGH,CRITICAL \
                        ${DOCKER_IMAGE}:${DOCKER_TAG} || true
                    """
                }
            }
        }
        
        stage('Deploy to Dev') {
            when {
                branch 'jenkins_integration_main'
            }
            steps {
                echo 'Deploying to Development environment...'
                script {
                    sh """
                        docker stop ${APP_NAME}-dev || true
                        docker rm ${APP_NAME}-dev || true
                        docker run -d \
                            --name ${APP_NAME}-dev \
                            -p 8080:8080 \
                            -e MONGODB_URI=${MONGODB_URI} \
                            -e SPRING_PROFILES_ACTIVE=dev \
                            ${DOCKER_IMAGE}:${DOCKER_TAG}
                    """
                }
            }
        }
        
        stage('Integration Tests') {
            when {
                branch 'jenkins_integration_main'
            }
            steps {
                echo 'Running integration tests...'
                script {
                    // Wait for application to start
                    sleep(time: 30, unit: 'SECONDS')
                    
                    sh """
                        # Health check
                        curl -f http://localhost:8080/health || exit 1
                        
                        # API tests
                        mvn verify -P integration-tests
                    """
                }
            }
        }
        
        stage('Deploy to Staging') {
            when {
                branch 'jenkins_integration_main'
            }
            input {
                message "Deploy to Staging?"
                ok "Deploy"
            }
            steps {
                echo 'Deploying to Staging environment...'
                script {
                    sh """
                        docker stop ${APP_NAME}-staging || true
                        docker rm ${APP_NAME}-staging || true
                        docker run -d \
                            --name ${APP_NAME}-staging \
                            -p 8081:8080 \
                            -e MONGODB_URI=${MONGODB_URI} \
                            -e SPRING_PROFILES_ACTIVE=staging \
                            ${DOCKER_IMAGE}:${DOCKER_TAG}
                    """
                }
            }
        }
        
        stage('Deploy to Production') {
            when {
                branch 'jenkins_integration_main'
            }
            input {
                message "Deploy to Production?"
                ok "Deploy"
                submitter "admin"
            }
            steps {
                echo 'Deploying to Production environment...'
                script {
                    // Blue-Green Deployment Strategy
                    sh """
                        # Deploy new version (Green)
                        docker run -d \
                            --name ${APP_NAME}-green \
                            -p 8082:8080 \
                            -e MONGODB_URI=${MONGODB_URI} \
                            -e SPRING_PROFILES_ACTIVE=production \
                            ${DOCKER_IMAGE}:${DOCKER_TAG}
                        
                        # Wait for green to be ready
                        sleep 30
                        
                        # Health check on green
                        curl -f http://localhost:8082/health || exit 1
                        
                        # Switch traffic (update load balancer or reverse proxy)
                        # This depends on your infrastructure setup
                        
                        # Remove old blue deployment
                        docker stop ${APP_NAME}-prod || true
                        docker rm ${APP_NAME}-prod || true
                        
                        # Rename green to prod
                        docker rename ${APP_NAME}-green ${APP_NAME}-prod
                    """
                }
            }
        }
    }
    
    post {
        always {
            echo 'Cleaning up workspace...'
            cleanWs()
        }
        success {
            echo 'Pipeline completed successfully!'
            emailext(
                subject: "✅ SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                body: """
                    <p>SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'</p>
                    <p>Commit: ${env.GIT_COMMIT_MSG}</p>
                    <p>Author: ${env.GIT_AUTHOR}</p>
                    <p>Check console output at: <a href='${env.BUILD_URL}'>${env.BUILD_URL}</a></p>
                """,
                to: 'team@example.com',
                mimeType: 'text/html'
            )
        }
        failure {
            echo 'Pipeline failed!'
            emailext(
                subject: "❌ FAILURE: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                body: """
                    <p>FAILURE: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'</p>
                    <p>Commit: ${env.GIT_COMMIT_MSG}</p>
                    <p>Author: ${env.GIT_AUTHOR}</p>
                    <p>Check console output at: <a href='${env.BUILD_URL}'>${env.BUILD_URL}</a></p>
                """,
                to: 'team@example.com',
                mimeType: 'text/html'
            )
        }
    }
}
