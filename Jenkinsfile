pipeline {

    agent any

    parameters {
        string(
            name: 'IMAGE_TAG',
            defaultValue: '1.2',
            description: 'Enter Docker image tag: 1.0, 1.1, 1.2, etc.'
        )
    }

    environment {
        AWS_REGION     = 'ap-south-1'
        AWS_ACCOUNT_ID = '392900064738'
        ECR_REPOSITORY = 'team-java-app'
        ECR_REGISTRY   = '392900064738.dkr.ecr.ap-south-1.amazonaws.com'
    }

    stages {

        stage('Checkout') {
            steps {
                echo '========== CHECKOUT =========='
                checkout scm
            }
        }

        stage('Maven Build') {
            steps {
                echo '========== MAVEN BUILD =========='

                sh '''
                    mvn clean package

                    echo "Generated JAR:"
                    ls -lh target/
                '''
            }
        }

        stage('Docker Build') {

            steps {

                script {
                    if (!(params.IMAGE_TAG ==~ /^[0-9]+\\.[0-9]+(\\.[0-9]+)?$/)) {
                        error(
                            "Invalid IMAGE_TAG: ${params.IMAGE_TAG}. " +
                            "Use 1.0, 1.1, 1.2, etc."
                        )
                    }
                }

                echo '========== DOCKER BUILD =========='

                sh '''
                    docker build \
                      --build-arg APP_VERSION=${IMAGE_TAG} \
                      -t ${ECR_REPOSITORY}:${IMAGE_TAG} .

                    docker images ${ECR_REPOSITORY}
                '''
            }
        }

        stage('ECR Login') {
            steps {

                echo '========== ECR LOGIN =========='

                sh '''
                    aws sts get-caller-identity

                    aws ecr get-login-password \
                      --region ${AWS_REGION} \
                    | docker login \
                      --username AWS \
                      --password-stdin ${ECR_REGISTRY}
                '''
            }
        }

        stage('Docker Tag') {
            steps {

                echo '========== DOCKER TAG =========='

                sh '''
                    docker tag \
                      ${ECR_REPOSITORY}:${IMAGE_TAG} \
                      ${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}

                    echo "Tagged image:"
                    docker images | grep ${ECR_REPOSITORY}
                '''
            }
        }

        stage('Docker Push') {
            steps {

                echo '========== DOCKER PUSH =========='

                sh '''
                    docker push \
                      ${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}
                '''
            }
        }
    }

    post {

        success {
            echo """
===================================================
PIPELINE SUCCESS
===================================================
Repository : ${ECR_REPOSITORY}
Image Tag  : ${params.IMAGE_TAG}
AWS Region : ${AWS_REGION}

Image:
${ECR_REGISTRY}/${ECR_REPOSITORY}:${params.IMAGE_TAG}
===================================================
"""
        }

        failure {
            echo """
===================================================
PIPELINE FAILED
Check the failed stage in Jenkins Console Output.
===================================================
"""
        }
    }
}
