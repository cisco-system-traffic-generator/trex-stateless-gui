#!/bin/sh
# updates version files based on git tag/revision

VERSION="$(git describe --tags --long)"

sed -i.bak -e "s/^version = .*/version = '$VERSION'/" build.gradle
sed -i.bak -e "1,/build/ s/<version>.*<\/version>/<version>$VERSION<\/version>/" pom.xml
sed -i.bak -e "s/#define TrexAppVersion .*/#define TrexAppVersion \"$VERSION\"/" src/main/deploy/package/windows/trex-stateless-gui.iss

