# AWS IAM Basics

## Core Concepts
- **User** — individual identity for a person or application
- **Group** — collection of users sharing same permissions
- **Policy** — JSON document defining what actions are allowed
- **Role** — temporary identity assumed by services or applications

## Security Best Practices
- Never use root account for daily work
- Always attach policies to groups not individual users
- Principle of Least Privilege — give only permissions needed
- Always create access keys for CLI, never use root keys

## CLI Commands Learned
```bash
# Verify who you are
aws sts get-caller-identity

# List S3 buckets
aws s3 ls

# Create S3 bucket
aws s3 mb s3://bucket-name --region ap-south-1

# Upload file to S3
aws s3 cp filename.txt s3://bucket-name/

# List bucket contents
aws s3 ls s3://bucket-name/

# List EC2 instances
aws ec2 describe-instances --region ap-south-1 --output table

# List IAM users
aws iam list-users
```

## Resources Created Tonight
- IAM Group: devops-learners
- IAM User: ranjay-devops-cli
- S3 Bucket: ranjay-devops-learning-bucket
