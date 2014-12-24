#!/bin/sh
# This script do a full build of Studio (including the MANIFEST generation and the P2 local repository construction)

mvn -f pom-first.xml clean install
mvn clean install
