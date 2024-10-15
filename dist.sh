#!/bin/bash
rm -rf dist
mkdir -p dist/js
npx shadow-cljs release app
cp build/js/main.js dist/js
cp -R public/* dist
