#!/usr/bin/env bash
. .env

dn=`dirname "$0"`
profile=default
bucket=${IDML2XML_SAM_PREFIX}-bucket

#sam package \
#  --profile ${AWS_PROFILE-$profile} \
#  --s3-bucket ${bucket} \
#  --output-template packaged.yml \
#  --template-file template.yml
sam deploy \
  --profile ${AWS_PROFILE-$profile} \
  --region us-east-1 \
  --capabilities CAPABILITY_IAM \
  --s3-bucket ${bucket} \
  --stack-name ${IDML2XML_SAM_PREFIX}-stack
