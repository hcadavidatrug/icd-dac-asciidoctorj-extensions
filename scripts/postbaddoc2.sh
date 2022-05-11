#!/bin/bash


export CI_PIPELINE_ID=32432432
export CI_PAGES_URL='http://pages.url.com/project_one' 
export CI_PROJECT_URL='http://project_one.github.com'  
export CI_COMMIT_AUTHOR='John Connor'
export CI_PIPELINE_CREATED_AT='2022-02-22T10:51:32Z'


export CI_PROJECT_NAME=$1
export CI_COMMIT_TAG=$2
export CI_PIPELINE_ID=$3

mvn -e -DASCIIDOC_SOURCE_PATH=$PWD/sample-docs-errors/rdl-conversion-errors -DOUTPUT_PATH=/tmp/output -DBACKEND_URL=https://documentation-dashboard.herokuapp.com -DBACKEND_CREDENTIALS=user:123 -DPROJECT_NAME=$CI_PROJECT_NAME -DPIPELINE_ID=$CI_PIPELINE_ID -DDEPLOYMENT_URL=$CI_PAGES_URL -DSOURCE_URL=$CI_PROJECT_URL  -DCOMMIT_AUTHOR="'$CI_COMMIT_AUTHOR'" -DCREATION_DATE=$CI_PIPELINE_CREATED_AT -DCOMMIT_TAG=$CI_COMMIT_TAG -DLOG_TAGS_FILE_PATH=$PWD/logs.txt


ec=$?
if [[ $ec -eq 0 ]] ;  then
  echo Success - Error code 0  
else
  echo 'Build failed - check logging details in the dashboard' 
  exit $ec 
fi
