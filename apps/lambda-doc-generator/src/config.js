export const config = {
  s3Bucket: process.env.S3_DOCS_BUCKET,
  s3Prefix: process.env.S3_PREFIX || 'generated',
  region: process.env.AWS_REGION || 'us-east-2'
};
