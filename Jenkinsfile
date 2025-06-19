pipeline {
  agent any

  tools {
    jdk 'Java17'       // match your Jenkins JDK name
    maven 'Maven3'     // match your Jenkins Maven name
  }

  environment {
    SONAR_TOKEN = credentials('sonar-token-id')
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
          sh """
            mvn sonar:sonar \
              -Dsonar.projectKey=hello-world \
              -Dsonar.login=${SONAR_TOKEN}
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
