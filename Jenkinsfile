pipeline {
  agent any

  environment {
    AWS_ACCOUNT_ID = '615595685715'     // ← your real AWS account ID
    AWS_REGION     = 'us-east-1'
    ECR_REPO       = 'hello-world-app'
    IMAGE_TAG      = "${env.BUILD_NUMBER}"
  }

  tools {
    jdk    'Java17'
    maven  'Maven3'
  }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Build') {
      steps { sh 'mvn clean compile -B' }
    }

    stage('Unit Test') {
      steps { sh 'mvn test -B' }
    }

    stage('SonarQube Analysis') {
      steps {
        withSonarQubeEnv('MySonar') {
          withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_AUTH_TOKEN')]) {
            sh '''
              mvn sonar:sonar \
                -Dsonar.projectKey=hello-world \
                -Dsonar.login=$SONAR_AUTH_TOKEN \
                -B
            '''
          }
        }
      }
    }

    stage('Package WAR') {
      steps { sh 'mvn package -DskipTests -B' }
    }

    stage('Build & Push Docker Image') {
      steps {
        withCredentials([usernamePassword(
          credentialsId: 'ECR_CREDENTIALS',
          usernameVariable: 'AWS_ACCESS_KEY_ID',
          passwordVariable: 'AWS_SECRET_ACCESS_KEY'
        )]) {
          sh '''
            aws configure set aws_access_key_id $AWS_ACCESS_KEY_ID
            aws configure set aws_secret_access_key $AWS_SECRET_ACCESS_KEY
            aws configure set default.region $AWS_REGION

            aws eks update-kubeconfig --name hello-world-eks --region $AWS_REGION

            aws ecr get-login-password --region $AWS_REGION \
              | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com

            docker build -t $ECR_REPO:$IMAGE_TAG .
            docker tag $ECR_REPO:$IMAGE_TAG $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPO:$IMAGE_TAG
            docker push $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPO:$IMAGE_TAG
          '''
        }
      }
    }

    stage('Deploy to Kubernetes') {
      steps {
        sh '''
          kubectl set image deployment/hello-world-deployment \
            hello-world-app=$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPO:$IMAGE_TAG \
            --record

          kubectl rollout status deployment/hello-world-deployment
        '''
      }
    }
  }

  post {
    success {
      echo "✅ Deployment to EKS succeeded!"
    }
    failure {
      echo "❌ Build or deployment failed – check the logs above."
    }
  }
}
