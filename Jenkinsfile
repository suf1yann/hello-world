pipeline {
  agent any

  tools {
    jdk 'Java17'
    maven 'Maven3'
  }

  environment {
    SONAR_AUTH_TOKEN = credentials('SONAR_TOKEN')
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
          sh 'mvn sonar:sonar -Dsonar.projectKey=hello-world -Dsonar.login=$SONAR_AUTH_TOKEN'
        }
      }
    }

    stage('Deploy to Tomcat') {
      steps {
        sh '''
          sudo cp target/*.war /opt/tomcat/webapps/hello-world.war
          sudo chown tomcat:tomcat /opt/tomcat/webapps/hello-world.war
          sudo systemctl restart tomcat
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
