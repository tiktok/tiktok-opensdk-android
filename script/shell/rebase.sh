#!/bin/bash
git add .
git stash
git fetch 
git rebase
git stash pop
