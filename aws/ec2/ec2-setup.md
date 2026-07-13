# AWS EC2 Setup — Day 3

## What Was Built
Ubuntu 22.04 EC2 instance launched in public subnet, SSH access from WSL.

## Resources Created

| Resource | ID | Details |
|----------|-----|---------|
| AMI | ami-0a9723306502e2558 | Ubuntu 22.04 LTS, ap-south-1 |
| Key Pair | ranjay-key | Private key: ~/ranjay-key.pem |
| Security Group | sg-01ad6ecf5df4325ad | SSH (22) from single IP only |
| Instance | i-061723145a86b8d33 | t3.micro, public subnet |
| Public IP | 13.233.107.226 | Auto-assigned |
| Private IP | 10.0.0.251 | Internal to VPC |

## Key Lessons

- Account's free-tier instance type was t3.micro, not t2.micro (varies by account)
- AMI IDs are region-specific and change over time — query dynamically instead of hardcoding
- Security group /32 CIDR restricts SSH to exactly one IP
- Public IP only works because: public subnet + IGW + route table + auto-assign-IP all chained together correctly

## CLI Commands Used

\`\`\`bash
# Find current Ubuntu AMI
aws ec2 describe-images --owners 099720109477 \
  --filters "Name=name,Values=ubuntu/images/hvm-ssd/ubuntu-jammy-22.04-amd64-server-*" "Name=state,Values=available" \
  --query 'sort_by(Images, &CreationDate)[-1].[ImageId,Name]' --output table --region ap-south-1

# Create key pair
aws ec2 create-key-pair --key-name ranjay-key --query 'KeyMaterial' --output text --region ap-south-1 > ~/ranjay-key.pem
chmod 400 ~/ranjay-key.pem

# Create security group + SSH rule
aws ec2 create-security-group --group-name ranjay-ssh-sg --description "Allow SSH access" --vpc-id <vpc-id> --region ap-south-1
aws ec2 authorize-security-group-ingress --group-id <sg-id> --protocol tcp --port 22 --cidr <my-ip>/32 --region ap-south-1

# Launch instance
aws ec2 run-instances --image-id <ami-id> --instance-type t3.micro --key-name ranjay-key \
  --security-group-ids <sg-id> --subnet-id <public-subnet-id> \
  --tag-specifications 'ResourceType=instance,Tags=[{Key=Name,Value=ranjay-web-server}]' --region ap-south-1

# SSH in
ssh -i ~/ranjay-key.pem ubuntu@<public-ip>
\`\`\`
