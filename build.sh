#!/bin/sh
# This script do a fuild build of Studio (including the MANIFEST generation and the P2 local repository construction)

cd eclipse-target-platform
mvn -f pom-first.xml clean install
cd ..
mvn -f pom-first.xml clean bundle:manifest
mvn clean install
