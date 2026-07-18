# AWS RDS — Day 6 (Friday, Week 5)

## What Was Built
A private, non-publicly-accessible MySQL RDS instance, reachable only from
inside the VPC — connected to successfully from the bastion host.

## Architecture

\`\`\`
Laptop --(ssh -A, agent forwarding)--> Bastion (public subnet)
                                             │
                                             mysql client, port 3306
                                             ▼
                                        RDS MySQL (private subnet, no public IP)
\`\`\`

## Core Concepts

**RDS (Relational Database Service)** — a managed database service. AWS
handles patching, backups, storage, and failover; you just use the
database engine (MySQL, PostgreSQL, MariaDB, etc).

**DB Subnet Group** — a named collection of subnets, spanning at least 2
Availability Zones, that RDS is allowed to place a database instance
into. Required even for a single-AZ, non-replicated instance, because RDS
is architected with high availability in mind from the start.

**--no-publicly-accessible** — the flag that keeps an RDS instance
unreachable from the internet even if its subnet group technically
includes a public subnet. This is the RDS equivalent of a private EC2
instance having no public IP.

**Security-group-to-security-group rules** — instead of allowing an IP
range (\`--cidr\`), you can allow traffic FROM another security group
(\`--source-group\`). This means "anything that is a member of that group
can reach me," regardless of what IP it currently has. More robust than
IP-based rules since it survives IP changes (e.g. after a stop/start).

## Resources

| Resource | Details |
|----------|---------|
| RDS Instance ID | ranjay-mysql-db |
| Engine | MySQL 8.4 |
| Endpoint | ranjay-mysql-db.cv8e6m0wu68w.ap-south-1.rds.amazonaws.com |
| Port | 3306 |
| Master Username | admin |
| DB Subnet Group | ranjay-db-subnet-group (subnet-0590baf9dc5f29e17 + subnet-014bc34b50cf94a39) |
| RDS Security Group | sg-015bc585d2700f927 |
| Publicly Accessible | No |
| Bastion Instance | i-0c042e8a77489dc85 (sg-0139dc97dcbf63bbf) |
| Private EC2 Instance | i-0f9e2d8253ee91596 (sg-06b2c7a1c81d1d483) |

## Real Problems Hit Tonight (and the actual lessons)

1. **IAM AccessDenied on RDS actions** — the CLI user (\`ranjay-devops-cli\`)
   inherits permissions from its IAM group (\`devops-learners\`), which had
   EC2/S3/IAM-read policies but nothing for RDS. Fixed by attaching
   \`AmazonRDSFullAccess\` to the GROUP (not the user directly) — correct
   practice, since managing permissions at the group level scales better
   than per-user policies.

2. **IAM AccessDenied on attach-group-policy itself** — the CLI user
   correctly could NOT grant itself more permissions (only had
   IAMReadOnlyAccess). This is intentional privilege-escalation
   prevention, not a bug. Fixed by attaching the policy from the AWS
   Console using a more privileged login, not the CLI user.

3. **Private EC2 instance had no internet access** — confirmed again
   tonight (as on bastion night) that the private subnet has no route to
   an Internet Gateway, so \`apt install mysql-client\` failed there. The
   production-correct fix is a NAT Gateway (paid, out of scope tonight).
   Practical fix used: install the MySQL client on bastion instead, which
   already has internet access via the Internet Gateway, and connect to
   RDS from there.

4. **Wrong security group ID used from stale notes** — had to re-derive
   the bastion's actual security group ID via \`describe-instances\` rather
   than trusting a written-down ID from a previous session, since the
   instance/tag had actually changed since Day 3.

## CLI Commands Used

\`\`\`bash
# Create DB Subnet Group (requires subnets in 2+ AZs)
aws rds create-db-subnet-group \
  --db-subnet-group-name ranjay-db-subnet-group \
  --db-subnet-group-description "Subnet group for RDS MySQL" \
  --subnet-ids subnet-014bc34b50cf94a39 subnet-0590baf9dc5f29e17 \
  --region ap-south-1

# Create RDS-specific security group
aws ec2 create-security-group \
  --group-name ranjay-rds-sg \
  --description "Allow MySQL access from private EC2 only" \
  --vpc-id vpc-01a1b762c4bcbaf03 \
  --region ap-south-1

# Allow MySQL (3306) from a specific security group (not an IP range)
aws ec2 authorize-security-group-ingress \
  --group-id <rds-sg-id> \
  --protocol tcp \
  --port 3306 \
  --source-group <ec2-or-bastion-sg-id> \
  --region ap-south-1

# Launch RDS instance, kept private
aws rds create-db-instance \
  --db-instance-identifier ranjay-mysql-db \
  --db-instance-class db.t3.micro \
  --engine mysql \
  --master-username admin \
  --master-user-password '<password>' \
  --allocated-storage 20 \
  --vpc-security-group-ids <rds-sg-id> \
  --db-subnet-group-name ranjay-db-subnet-group \
  --no-publicly-accessible \
  --region ap-south-1

# Poll status until "available"
aws rds describe-db-instances \
  --db-instance-identifier ranjay-mysql-db \
  --query 'DBInstances[0].[DBInstanceStatus,Endpoint.Address]' \
  --output table \
  --region ap-south-1

# Connect from bastion (after installing mysql-client if needed)
mysql -h <rds-endpoint> -u admin -p
\`\`\`

## Key Lessons

- IAM permissions should be managed at the GROUP level, not attached to
  individual users — confirmed hands-on tonight when the fix was to
  modify \`devops-learners\`, not \`ranjay-devops-cli\` directly.
- A CLI user correctly cannot escalate its own permissions — that
  requires a more privileged identity, by design, to prevent privilege
  escalation attacks.
- RDS requires a DB Subnet Group spanning 2+ AZs even for a single,
  non-Multi-AZ instance — this is a structural AWS requirement, not
  optional.
- \`--no-publicly-accessible\` is what actually keeps RDS private, not
  which subnets are in its subnet group.
- Security-group-to-security-group rules (\`--source-group\`) are more
  robust than IP-based rules for internal AWS-to-AWS traffic.
- Private subnets have zero outbound internet access, not just inbound —
  confirmed twice now (EC2 last night, RDS/apt tonight). A NAT Gateway is
  the real production fix; a bastion with internet access is a reasonable
  workaround for occasional admin tasks in a learning environment.
