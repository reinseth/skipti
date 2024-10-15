#!/bin/bash
rm -rf docs
mkdir -p docs/js
npx shadow-cljs release app
cp build/js/main.js docs/js
cp -R public/* docs
