pipeline {
  agent any

  tools {
    jdk 'Java17'
    maven 'Maven3'
  }

  environment {
    DEPLOY_PATH = '/opt/tomcat/webapps'
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build & Test') {
      steps {
        sh 'mvn clean test'
      }
    }

    stage('SonarQube Analysis') {
      steps {
        withSonarQubeEnv('MySonar') {
          withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_AUTH_TOKEN')]) {
            sh 'mvn sonar:sonar -Dsonar.projectKey=hello-world -Dsonar.login=$SONAR_AUTH_TOKEN'
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
          cp target/*.war $DEPLOY_PATH/hello-world.war
          echo "WAR deployed to Tomcat webapps"
        '''
      }
    }
  }

  post {
    always {
      archiveArtifacts artifacts: 'target/*.war', fingerprint: true
    }
    success {
      echo 'Build succeeded!'
    }
    failure {
      echo 'Build failed!'
    }
  }
}
