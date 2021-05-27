#!/bin/sh
echo "********************************************************"
echo "Starting Configuration Server"
echo "********************************************************"
java -jar /usr/local/config-server/@project.build.finalName@.jar
