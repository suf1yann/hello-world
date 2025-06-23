pipeline {
  agent any

  tools {
    jdk   'Java17'
    maven 'Maven3'
  }

  environment {
    AWS_ACCOUNT_ID = '615595685715'
    AWS_REGION     = 'us-east-1'
    IMAGE_NAME     = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/hello-world-app"
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build & Package') {
      steps {
        sh 'mvn clean package -DskipTests'
      }
    }

    stage('Unit Test') {
      steps {
        sh 'mvn test'
      }
    }

    stage('SonarQube Analysis') {
      steps {
        withSonarQubeEnv('MySonar') {
          withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_AUTH_TOKEN')]) {
            sh "mvn sonar:sonar -Dsonar.projectKey=hello-world -Dsonar.login=${SONAR_AUTH_TOKEN}"
          }
        }
      }
    }

    stage('Build & Push Docker Image') {
      steps {
        // requires you to have an AWS credential (access key + secret) stored as 'ECR_CREDENTIALS'
        withCredentials([usernamePassword(
            credentialsId: 'ECR_CREDENTIALS',
            usernameVariable: 'AWS_ACCESS_KEY_ID',
            passwordVariable: 'AWS_SECRET_ACCESS_KEY'
        )]) {
          sh """
            aws ecr get-login-password --region ${AWS_REGION} \
              | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com
            docker build -t ${IMAGE_NAME}:latest .
            docker push ${IMAGE_NAME}:latest
          """
        }
      }
    }

    stage('Deploy to Kubernetes') {
      steps {
        // your kubeconfig file, stored as a "Secret file" credential in Jenkins under id 'KUBECONFIG'
        withCredentials([file(credentialsId: 'KUBECONFIG', variable: 'KUBECONFIG_PATH')]) {
          sh """
            export KUBECONFIG=${KUBECONFIG_PATH}
            kubectl apply -f k8s/deployment.yaml
            kubectl rollout status deployment/hello-world-deployment --timeout=120s
          """
        }
      }
    }
  }

  post {
    success {
      echo "✅ Pipeline completed successfully!"
    }
    failure {
      echo "❌ Pipeline failed. Check the console output for errors."
    }
  }
}
