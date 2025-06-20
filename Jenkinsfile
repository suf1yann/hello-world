pipeline {
  agent any

  tools {
    jdk 'Java17'
    maven 'Maven3'
  }

  environment {
    SONAR_PROJECT_KEY = 'hello-world'
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build') {
      steps {
        sh 'mvn clean compile'
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
            sh 'mvn sonar:sonar -Dsonar.projectKey=$SONAR_PROJECT_KEY -Dsonar.login=$SONAR_AUTH_TOKEN'
          }
        }
      }
    }

    stage('Package WAR') {
      steps {
        sh 'mvn package -DskipTests'
      }
    }

    stage('Deploy to Tomcat') {
      steps {
        sh '''
          cp target/*.war /opt/tomcat/webapps/
          chown tomcat:tomcat /opt/tomcat/webapps/*.war
        '''
      }
    }
  }

  post {
    success {
      archiveArtifacts artifacts: 'target/*.war', fingerprint: true
      slackSend (
        channel: '#ci-cd',
        color: 'good',
        message: "✅ SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' succeeded.\nCheck console: ${env.BUILD_URL}"
      )
    }
    failure {
      slackSend (
        channel: '#ci-cd',
        color: 'danger',
        message: "❌ FAILURE: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' failed.\nCheck console: ${env.BUILD_URL}"
      )
    }
  }
}
