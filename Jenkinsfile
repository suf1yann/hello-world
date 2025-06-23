pipeline {
  agent any

  environment {
    ECR_REGISTRY = '615595685715.dkr.ecr.us-east-1.amazonaws.com'
    IMAGE_NAME   = 'hello-world-app'
    SONAR_HOST_URL = 'http://3.91.241.216:9000'
  }

  tools {
    jdk   'Java17'
    maven 'Maven3'
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build') {
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
          withCredentials([ 
            string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_AUTH_TOKEN') 
          ]) {
            sh """
              mvn sonar:sonar \
                -Dsonar.projectKey=hello-world \
                -Dsonar.host.url=$SONAR_HOST_URL \
                -Dsonar.login=$SONAR_AUTH_TOKEN
            """
          }
        }
      }
    }

    stage('Docker Build & Push') {
      steps {
        withCredentials([usernamePassword(
          credentialsId: 'ECR_CREDENTIALS',     
          usernameVariable: 'AWS_ACCESS_KEY_ID',
          passwordVariable: 'AWS_SECRET_ACCESS_KEY'
        )]) {
          sh '''
            aws ecr get-login-password --region us-east-1 \
              | docker login --username AWS --password-stdin $ECR_REGISTRY

            docker build -t $IMAGE_NAME:$BUILD_NUMBER .
            docker tag $IMAGE_NAME:$BUILD_NUMBER $ECR_REGISTRY/$IMAGE_NAME:$BUILD_NUMBER
            docker push $ECR_REGISTRY/$IMAGE_NAME:$BUILD_NUMBER
          '''
        }
      }
    }

    stage('Deploy to Kubernetes') {
      steps {
        script {
          def cname = sh(
            script: "kubectl get deploy hello-world-deployment -o jsonpath='{.spec.template.spec.containers[0].name}'",
            returnStdout: true
          ).trim()

          sh """
            kubectl set image deployment/hello-world-deployment \
              ${cname}=${ECR_REGISTRY}/${IMAGE_NAME}:${BUILD_NUMBER} --record
          """
        }
      }
    }
  }

  post {
    always {
      archiveArtifacts artifacts: 'target/*.war', fingerprint: true
    }
  }
}
