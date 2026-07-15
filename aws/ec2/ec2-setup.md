# AWS EC2 Setup — Ranjay DevOps Journey

## What Is EC2 And Why It Exists
EC2 (Elastic Compute Cloud) lets you rent virtual computers in AWS data centers
instead of buying physical hardware. You choose the OS, size, and storage.
Ready in 60 seconds. Pay only for what you use. Scale up or down anytime.

---

## Four Things Needed Before Launching

| Component | Purpose | Analogy |
|-----------|---------|---------|
| AMI | OS template to boot from | USB drive with OS pre-installed |
| Key Pair | Secure login without password | Physical key to unlock the door |
| Security Group | Firewall rules for traffic | Building security guard |
| VPC + Subnet | Network to place instance in | Neighborhood and street |

---

## Architecture Built Tonight
Internet
│
▼
Internet Gateway
│
▼
Public Subnet (10.0.0.0/24)
│
▼
Security Group
├── Port 22 → 205.254.168.190/32 (SSH from my IP only)
└── Port 80 → 0.0.0.0/0 (HTTP from anywhere)
│
▼
EC2 Instance (t3.micro, Ubuntu 22.04)
├── Public IP: 3.110.220.60
├── Private IP: 10.0.0.28
└── Nginx running on port 80

---

## Resources Created

| Resource | ID | Details |
|----------|-----|---------|
| AMI | ami-0c809520a0d652e03 | Ubuntu 22.04 LTS, July 2026 |
| Key Pair | ranjay-key | Private key: ~/ranjay-key.pem, chmod 400 |
| Security Group | sg-004c19a47f9036365 | SSH port 22 + HTTP port 80 |
| Instance | i-09e53677d7809afa5 | t3.micro, public subnet |
| Public IP | 3.110.220.60 | Auto-assigned |
| Private IP | 10.0.0.28 | Internal to VPC |

---

## Step by Step CLI Commands

### Step 1 — Find Latest Ubuntu AMI (never hardcode AMI IDs)
```bash
aws ec2 describe-images \
  --owners 099720109477 \
  --filters \
    "Name=name,Values=ubuntu/images/hvm-ssd/ubuntu-jammy-22.04-amd64-server-*" \
    "Name=state,Values=available" \
  --query 'sort_by(Images, &CreationDate)[-1].[ImageId,Name]' \
  --output table \
  --region ap-south-1
```
Why 099720109477: This is Canonical's official AWS account. Always use this to avoid fake images.

### Step 2 — Create Key Pair
```bash
aws ec2 create-key-pair \
  --key-name ranjay-key \
  --query 'KeyMaterial' \
  --output text \
  --region ap-south-1 > ~/ranjay-key.pem

chmod 400 ~/ranjay-key.pem
```
Why chmod 400: SSH refuses to connect if key file has loose permissions. 400 = read only by owner.
Why > file: AWS shows private key only once. Redirect captures it immediately to disk.

### Step 3 — Get Your Public IP
```bash
curl -s https://checkip.amazonaws.com
```
Use this IP with /32 for SSH rule. /32 = single exact IP address.

### Step 4 — Create Security Group
```bash
aws ec2 create-security-group \
  --group-name ranjay-ssh-sg \
  --description "Allow SSH from my IP only" \
  --vpc-id vpc-01a1b762c4bcbaf03 \
  --region ap-south-1
```
Security groups are stateful — return traffic is automatically allowed.
Unlike NACLs which are stateless and need explicit rules both ways.

### Step 5 — Add SSH Rule (your IP only)
```bash
aws ec2 authorize-security-group-ingress \
  --group-id sg-004c19a47f9036365 \
  --protocol tcp \
  --port 22 \
  --cidr <your-ip>/32 \
  --region ap-south-1
```

### Step 6 — Add HTTP Rule (open to world)
```bash
aws ec2 authorize-security-group-ingress \
  --group-id sg-004c19a47f9036365 \
  --protocol tcp \
  --port 80 \
  --cidr 0.0.0.0/0 \
  --region ap-south-1
```
Port 22 restricted to your IP. Port 80 open to everyone. Never open port 22 to 0.0.0.0/0.

### Step 7 — Launch Instance
```bash
aws ec2 run-instances \
  --image-id ami-0c809520a0d652e03 \
  --instance-type t3.micro \
  --key-name ranjay-key \
  --security-group-ids sg-004c19a47f9036365 \
  --subnet-id subnet-0590baf9dc5f29e17 \
  --tag-specifications 'ResourceType=instance,Tags=[{Key=Name,Value=ranjay-web-server}]' \
  --region ap-south-1
```
t3.micro = free tier. Never accidentally launch c5 or r5 — they cost real money.

### Step 8 — Check Instance Status
```bash
aws ec2 describe-instances \
  --filters "Name=tag:Name,Values=ranjay-web-server" \
  --query 'Reservations[0].Instances[0].[InstanceId,State.Name,PublicIpAddress,PrivateIpAddress]' \
  --output table \
  --region ap-south-1
```

### Step 9 — SSH Into Instance
```bash
ssh -i ~/ranjay-key.pem ubuntu@<public-ip>
```
ubuntu = default username for Ubuntu AMIs.
Different AMIs have different default users:
  Ubuntu → ubuntu
  Amazon Linux → ec2-user
  CentOS → centos

### Step 10 — Verify Port Nginx Is Listening On
```bash
sudo ss -tlnp | grep nginx
```

---

## Key Concepts for Interviews

### Security Group vs NACL
| | Security Group | NACL |
|--|---------------|------|
| Level | Instance level | Subnet level |
| State | Stateful | Stateless |
| Rules | Allow only | Allow and Deny |
| Return traffic | Automatic | Must explicitly allow |

### Instance Types Naming Convention
t3.micro → t=family, 3=generation, micro=size
Never use c5, r5, m5 for learning — they cost money.

### AMI IDs
Always query dynamically. AMI IDs are region specific and change with updates.
Canonical owner ID: 099720109477

### Why Private IP Does Not Change But Public IP Does
Private IP is fixed for the life of the instance.
Public IP changes every time you stop and start the instance.
Use Elastic IP if you need a permanent public IP.

---

## Common Interview Questions

Q: How do you connect to an EC2 instance securely?
A: SSH using a key pair. Private key on your machine, public key on the instance.
   Never use password authentication. Restrict port 22 to your IP with /32.

Q: What is the difference between stop and terminate?
A: Stop = instance paused, data preserved, no compute charge but storage charge continues.
   Terminate = instance deleted permanently, root volume deleted by default.

Q: What happens to the public IP when you stop an instance?
A: It is released and you get a different one when you start again.
   Use Elastic IP for a permanent public IP address.

Q: Can you change instance type after launch?
A: Yes — stop the instance, change instance type, start again.
