# AWS VPC Setup — Ranjay DevOps Journey

## What is a VPC and Why It Exists
By default AWS is a shared environment. A VPC (Virtual Private Cloud) gives
you your own isolated private network inside AWS. Think of it as buying a
gated society in a city — you own the land, you decide who enters, you decide
what roads exist inside. Nobody from outside can walk in unless you explicitly
open the gate.

Without a VPC your resources are exposed. With a VPC everything is private
by default and you control every aspect of networking.

---

## Architecture We Built
Internet
│
▼
Internet Gateway (igw-05a2588f1833e8848)
│   The main gate connecting your VPC to the internet
▼
VPC: ranjay-vpc (vpc-01a1b762c4bcbaf03)
CIDR: 10.0.0.0/16 — gives 65,536 private IP addresses
│
├── Public Subnet (subnet-0590baf9dc5f29e17)
│   CIDR: 10.0.0.0/24 | AZ: ap-south-1a
│   → Has route to IGW (internet accessible)
│   → Auto public IP enabled
│   → Put web servers, load balancers here
│
└── Private Subnet (subnet-014bc34b50cf94a39)
CIDR: 10.0.2.0/24 | AZ: ap-south-1b
→ No route to IGW (internet blocked)
→ No public IPs assigned
→ Put databases, backend servers here

---

## Resources Created

| Resource | ID | CIDR / Details |
|----------|-----|---------------|
| VPC | vpc-01a1b762c4bcbaf03 | 10.0.0.0/16 |
| Public Subnet | subnet-0590baf9dc5f29e17 | 10.0.0.0/24, ap-south-1a |
| Private Subnet | subnet-014bc34b50cf94a39 | 10.0.2.0/24, ap-south-1b |
| Internet Gateway | igw-05a2588f1833e8848 | Attached to VPC |
| Route Table | rtb-0107e103cd0bc2bb7 | 0.0.0.0/0 → IGW |

---

## Step by Step — Exact Commands Used

### Step 1 — Create VPC
```bash
aws ec2 create-vpc \
  --cidr-block 10.0.0.0/16 \
  --tag-specifications 'ResourceType=vpc,Tags=[{Key=Name,Value=ranjay-vpc}]' \
  --region ap-south-1
```
What it does: Creates your private network with 65,536 IP addresses.
CIDR /16 = 65,536 addresses. /24 = 256 addresses. /32 = 1 address.

### Step 2 — Create Public Subnet
```bash
aws ec2 create-subnet \
  --vpc-id vpc-01a1b762c4bcbaf03 \
  --cidr-block 10.0.0.0/24 \
  --availability-zone ap-south-1a \
  --tag-specifications 'ResourceType=subnet,Tags=[{Key=Name,Value=ranjay-public-subnet-1}]'
```
What it does: Carves out 256 addresses in AZ ap-south-1a for public resources.
Why different AZ: If ap-south-1a goes down, your private subnet in ap-south-1b still works.

### Step 3 — Create Private Subnet
```bash
aws ec2 create-subnet \
  --vpc-id vpc-01a1b762c4bcbaf03 \
  --cidr-block 10.0.2.0/24 \
  --availability-zone ap-south-1b \
  --tag-specifications 'ResourceType=subnet,Tags=[{Key=Name,Value=ranjay-private-subnet-1}]'
```
What it does: Carves out 256 addresses in AZ ap-south-1b for private resources.
Why private: Databases should never be directly reachable from internet.

### Step 4 — Create Internet Gateway
```bash
aws ec2 create-internet-gateway \
  --tag-specifications 'ResourceType=internet-gateway,Tags=[{Key=Name,Value=ranjay-igw}]'
```
What it does: Creates the gate. But it is not connected to anything yet.
Think of it as: Building a gate but not installing it in the wall yet.

### Step 5 — Attach Internet Gateway to VPC
```bash
aws ec2 attach-internet-gateway \
  --internet-gateway-id igw-05a2588f1833e8848 \
  --vpc-id vpc-01a1b762c4bcbaf03
```
What it does: Installs the gate in your VPC wall.
Without this: Your VPC has a gate but it leads nowhere.

### Step 6 — Create Route Table
```bash
aws ec2 create-route-table \
  --vpc-id vpc-01a1b762c4bcbaf03 \
  --tag-specifications 'ResourceType=route-table,Tags=[{Key=Name,Value=ranjay-public-rt}]'
```
What it does: Creates road signs for your public subnet.
Auto created route: 10.0.0.0/16 → local (internal VPC traffic stays internal).

### Step 7 — Add Internet Route
```bash
aws ec2 create-route \
  --route-table-id rtb-0107e103cd0bc2bb7 \
  --destination-cidr-block 0.0.0.0/0 \
  --gateway-id igw-05a2588f1833e8848
```
What it does: Adds road sign — all external traffic goes through Internet Gateway.
0.0.0.0/0 means: Any IP address not matching local routes = send to IGW.

### Step 8 — Associate Route Table With Public Subnet
```bash
aws ec2 associate-route-table \
  --route-table-id rtb-0107e103cd0bc2bb7 \
  --subnet-id subnet-0590baf9dc5f29e17
```
What it does: Installs the road signs specifically in the public subnet.
Why only public: Private subnet should have no route to internet.

### Step 9 — Enable Auto Public IP on Public Subnet
```bash
aws ec2 modify-subnet-attribute \
  --subnet-id subnet-0590baf9dc5f29e17 \
  --map-public-ip-on-launch
```
What it does: Every EC2 instance launched in public subnet automatically gets
a public IP address so it can be reached from the internet.

### Verify Auto Public IP is enabled
```bash
aws ec2 describe-subnets \
  --subnet-ids subnet-0590baf9dc5f29e17 \
  --query 'Subnets[0].MapPublicIpOnLaunch'
# Should return: true
```

---

## Key Concepts to Remember for Interviews

### CIDR Blocks
- /16 = 65,536 IPs (VPC level)
- /24 = 256 IPs (subnet level)
- /32 = 1 IP (single host)
- Rule: subnet CIDR must be within VPC CIDR

### Public vs Private Subnet — The One Difference
A subnet is public if its route table has a route to an Internet Gateway.
That is the only difference. There is no "public" flag on a subnet.

### Internet Gateway vs NAT Gateway
| | Internet Gateway | NAT Gateway |
|--|-----------------|-------------|
| Direction | Both in and out | Only outbound |
| Used for | Public subnets | Private subnets |
| Cost | Free | Paid per hour |
| Use case | Web servers | Private EC2 needs internet for updates |

### Route Table Rules
- Most specific route wins
- 10.0.0.0/16 is more specific than 0.0.0.0/0
- Local route always takes priority for internal traffic

---

## Common Interview Questions on VPC

Q: What makes a subnet public?
A: A route in its route table pointing 0.0.0.0/0 to an Internet Gateway.

Q: Can a private subnet access the internet?
A: Yes — through a NAT Gateway placed in the public subnet.

Q: What is the difference between Security Group and NACL?
A: Security Group is stateful (return traffic allowed automatically).
   NACL is stateless (must explicitly allow both inbound and outbound).
   Security Group is at instance level. NACL is at subnet level.

Q: Can two VPCs communicate?
A: Yes — through VPC Peering or Transit Gateway.
