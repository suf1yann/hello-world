pipeline {
  agent any

  tools {
    jdk 'Java17'
    maven 'Maven3'
    sonar 'SonarQubeScanner'
  }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }
    stage('Build') {
      steps { sh 'mvn clean package -DskipTests' }
    }
    stage('SonarQube Analysis') {
      steps {
        withSonarQubeEnv('MySonar') {
          sh 'mvn sonar:sonar -Dsonar.projectKey=hello-world'
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
