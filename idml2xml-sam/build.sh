#!/usr/bin/env bash

if [ ! -d TranspectFunction/src/resources/idml2xml-frontend ]; then
  echo "Downloading Transpect dependencies..."
  echo
  git clone --recursive https://github.com/transpect/idml2xml-frontend TranspectFunction/src/resources/idml2xml-frontend
  echo
fi

sam build
