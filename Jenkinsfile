pipeline {
  agent any

  tools {
    maven 'Maven3'
    jdk 'Java17'
  }

  environment {
    SONAR_PROJECT_KEY = 'hello-world'
    SONAR_HOST_URL = 'http://44.202.102.252:9000'
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
            sh 'mvn sonar:sonar -Dsonar.projectKey=$SONAR_PROJECT_KEY -Dsonar.login=$SONAR_AUTH_TOKEN'
          }
        }
      }
    }

    stage('Deploy to Tomcat') {
      steps {
        sh '''
          WAR_FILE=$(ls target/*.war | head -n 1)
          cp "$WAR_FILE" /opt/tomcat/webapps/hello-world.war
        '''
      }
    }
  }

  post {
    always {
      archiveArtifacts artifacts: 'target/*.war', fingerprint: true
    }
  }
}
