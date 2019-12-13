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
                sh "rm -fr apache-maven*"
                sh "wget https://www-eu.apache.org/dist/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.zip"
                sh "unzip -qo apache-maven-3.6.3-bin.zip"
                sh "./apache-maven-3.6.3/bin/mvn package"
            }
        }

    } 
}