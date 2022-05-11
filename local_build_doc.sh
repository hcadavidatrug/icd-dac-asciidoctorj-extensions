#!/bin/bash
mvn -e -DASCIIDOC_SOURCE_PATH=$1 -DOUTPUT_PATH=$2 
ec=$?
if [[ $ec -eq 0 ]] ;  then
  echo Success - Error code 0  
else
  echo 'Build failed - check logging details in the dashboard' 
  exit $ec 
fi
