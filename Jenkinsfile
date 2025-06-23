pipeline {
  agent any

  tools {
    jdk 'Java17'
    maven 'Maven3'
  }

  environment {
    AWS_DEFAULT_REGION = 'us-east-1'
    AWS_ACCOUNT_ID     = '615595685715'                    
    ECR_REPO           = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/hello-world-app"
    IMAGE_TAG          = "${BUILD_NUMBER}"
    KUBE_CONFIG        = '/var/lib/jenkins/.kube/config'
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build & Test') {
      parallel {
        stage('Compile & Package') {
          steps {
            sh 'mvn clean package -DskipTests -B'
          }
        }
        stage('Unit Tests') {
          steps {
            sh 'mvn test -B'
          }
        }
      }
    }

    stage('SonarQube Analysis') {
      steps {
        withSonarQubeEnv('MySonar') {
          withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_AUTH_TOKEN')]) {
            sh 'mvn sonar:sonar -Dsonar.projectKey=hello-world -Dsonar.login=$SONAR_AUTH_TOKEN -B'
          }
        }
      }
    }

    stage('Docker Build & Push to ECR') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'ECR_CREDENTIALS', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
          sh '''
            # configure AWS CLI
            aws configure set aws_access_key_id $AWS_ACCESS_KEY_ID
            aws configure set aws_secret_access_key $AWS_SECRET_ACCESS_KEY
            aws configure set default.region $AWS_DEFAULT_REGION

            # authenticate Docker to ECR
            aws ecr get-login-password --region $AWS_DEFAULT_REGION | docker login --username AWS --password-stdin $ECR_REPO

            # build, tag, and push
            docker build -t hello-world-app:${IMAGE_TAG} .
            docker tag hello-world-app:${IMAGE_TAG} $ECR_REPO:${IMAGE_TAG}
            docker push $ECR_REPO:${IMAGE_TAG}
          '''
        }
      }
    }

    stage('Deploy to Kubernetes') {
      steps {
        sh """
          kubectl set image \
            deployment/hello-world-deployment \
            hello-world-app=$ECR_REPO:${IMAGE_TAG} \
            --record
        """
      }
    }
  }

  post {
    success {
      echo "✅ Deployment succeeded! Your pods will roll out to image:$IMAGE_TAG"
    }
    failure {
      echo "❌ Build or deploy failed – check the logs above!"
    }
  }
}
