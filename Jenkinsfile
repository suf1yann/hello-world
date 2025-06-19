pipeline {
  agent any

  tools {
    jdk 'Java17'
    maven 'Maven3'
  }

  environment {
    PROJECT_KEY = 'hello-world'
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

    stage('SonarQube Analysis') {
      steps {
        withSonarQubeEnv('MySonar') {
          withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_AUTH_TOKEN')]) {
            sh 'mvn sonar:sonar -Dsonar.projectKey=$PROJECT_KEY -Dsonar.login=$SONAR_AUTH_TOKEN'
          }
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
