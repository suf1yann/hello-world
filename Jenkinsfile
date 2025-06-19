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
          cp target/hello-world.war /opt/tomcat/webapps/hello-world.war
          chown tomcat:tomcat /opt/tomcat/webapps/hello-world.war
        '''
      }
    }
  }

  post {
    always {
      archiveArtifacts artifacts: 'target/*.war', fingerprint: true
    }

    failure {
      echo 'Build failed. Please check console output.'
    }
  }
}
