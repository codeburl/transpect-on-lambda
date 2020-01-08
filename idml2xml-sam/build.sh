#!/usr/bin/env bash


if [ ! -d TranspectFunction/src/resources/idml2xml-frontend ]; then
  echo "Downloading Transpect dependencies..."
  echo
  git clone --recursive https://github.com/transpect/idml2xml-frontend TranspectFunction/src/resources/idml2xml-frontend
  echo
fi

# I eventually gave up on having Maven help me with this unpublished JAR
if [ ! -d TranspectFunction/src/main/java/com/codeburl/UnZip.java ]; then
  echo "Linking UnZip extension..."
  echo "// DO NOT EDIT" > TranspectFunction/src/main/java/com/codeburl/UnZip.java
  echo "// SEE https://github.com/transpect/unzip-extension for ORIGINAL FILE" >> TranspectFunction/src/main/java/com/codeburl/UnZip.java
  echo "// FILE INCLUDED HERE TO SIMPLIFY PACKAGING" >> TranspectFunction/src/main/java/com/codeburl/UnZip.java
  sed 's/package io.transpect.calabash.extensions;/package com.codeburl;/' TranspectFunction/src/resources/idml2xml-frontend/calabash/extensions/transpect/unzip-extension/src/main/java/io/transpect/calabash/extensions/UnZip.java >> TranspectFunction/src/main/java/com/codeburl/UnZip.java
fi

sam build
