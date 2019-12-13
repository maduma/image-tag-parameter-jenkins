pipeline {
    agent any

    stages {

        stage("build Jenkins Plugin") {
            agent {
                docker {
                    image "openjdk:8u232"
                }
            }
            steps {
                sh "wget https://www-eu.apache.org/dist/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.zip"
                sh "unzip -q apache-maven-3.6.3-bin.zip"
                sh "PATH=/apache-maven-3.6.3/bin:$PATH mvn package"
            }
        }

    } 
}