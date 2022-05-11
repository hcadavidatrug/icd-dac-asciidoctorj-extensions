#!/bin/bash
echo Extracting git tags from git repository in folder $1 and saving on $2
#git --git-dir $1/.git  tag -l --format='{"tag": "%(tag)", "subject": "%(subject)", "created": "%(taggerdate)", "creator":"%(tagger)"}' > $2
git --git-dir $1/.git  tag -l --format='{"tag": "%(tag)", "subject": "%(subject)", "created": "%(creatordate:short)"}' > $2


