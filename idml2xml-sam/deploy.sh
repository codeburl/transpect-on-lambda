#!/usr/bin/env bash
. .env

bucket=${IDML2XML_SAM_PREFIX}-bucket

sam deploy \
  --capabilities CAPABILITY_IAM \
  --profile $AWS_PROFILE \
  --region $AWS_DEFAULT_REGION \
  --s3-bucket ${bucket} \
  --stack-name ${IDML2XML_SAM_PREFIX}-stack
