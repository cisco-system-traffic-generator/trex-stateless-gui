# Developer's Environment bringup

## Install Java Development Kit (JDK)

### Ubuntu 18.04 LTS

It turns out that OpenJDK cannot be used for development since OpenJDK is not shipped with JavaFX. And OpenJavaFX works only with OpenJDK 11+. Luckily, Oracle JDK has builtin JavaFX. But installation is a little bit tricky:

1. Find where to download Oracle JDK8. You should find a link to Oracle's website, mine was [this](https://www.oracle.com/ru/java/technologies/javase/javase-jdk8-downloads.html)

2. Yes you'll need Oracle account due to Oracle's policy changes (that's why PPA does no longer work)

3. Download respective **JDK** (`tar.gz` for your processor).

```bash
#!/bin/bash

ls jdk-8u281-linux-x64.tar.gz
sudo mkdir -p /usr/lib/jvm
sudo tar zxvf jdk-8u281-linux-x64.tar.gz -C /usr/lib/jvm
sudo update-alternatives --install "/usr/bin/java" "java" "/usr/lib/jvm/jdk1.8.0_281/bin/java" 1
sudo update-alternatives --set java /usr/lib/jvm/jdk1.8.0_281/bin/java
```

4. Write `JAVA_HOME` path to `~/.profile` (mine was `/usr/lib/jvm/jdk1.8.0_281`)

```bash
#!/bin/bash

echo export JAVA_HOME=/usr/lib/jvm/jdk1.8.0_281 >> ~/.profile
source ~/.profile

echo $JAVA_HOME
```

# Clone Nested Projects

Clone `trex-java-sdk` and `trex-packet-editor` to the same directory where `trex-stateless-gui` was cloned to.

```bash
#!/bin/bash

git clone https://github.com/cisco-system-traffic-generator/trex-java-sdk.git
git clone https://github.com/cisco-system-traffic-generator/trex-packet-editor.git

ls
# trex-java-sdk  trex-packet-editor  trex-stateless-gui 
```

# Use Gradlew

```bash
#!/bin/bash

cd trex-stateless-gui

# all tasks
./gradlew tasks

# Run the application
./gradlew run

# Run unittests
./gradlew test

# Run e2e tests (additional env required)
./gradlew e2eTest
```