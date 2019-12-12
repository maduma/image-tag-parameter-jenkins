# how to build the Jenkins Plugin
 
## Install skdman
```
sudo apt install zip
curl -s "https://get.sdkman.io" | bash
source .sdkman/bin/sdkman-init.sh 
```

## install JDK and Maven
```
sdk install java 8.0.232-open
javac -version
sdk install maven
mvn -version
```

## test and build
```
mvn verify
mvn hpi:run
```

## access the plugin
Point the url to localhost:8080/jenkins
