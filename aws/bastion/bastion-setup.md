# AWS Bastion Host + Private EC2 — Day 5 (Thursday, Week 5)

## What Was Built
A bastion host in the public subnet used as the only entry point to reach
a second EC2 instance living in the private subnet with no public IP and
no internet route.

## Architecture

\`\`\`
Laptop --(ssh -A)--> Bastion (public subnet) --(ssh, agent forwarded)--> Private EC2 (private subnet)
\`\`\`

## Resources

| Resource | Details |
|----------|---------|
| Bastion Public IP | 65.2.148.236 |
| Bastion Private IP | 10.0.0.226 |
| Bastion Subnet | subnet-0590baf9dc5f29e17 (public) |
| Private Instance ID | i-0f9e2d8253ee91596 |
| Private Instance Public IP | None (by design) |
| Private Instance Private IP | 10.0.2.195 |
| Private Instance Subnet | subnet-014bc34b50cf94a39 (private) |

## Key Concept — SSH Agent Forwarding

Bastion needs to authenticate onward into the private instance, but should
never physically hold the private key file — if bastion (public-facing) is
ever compromised, a stored key there compromises every server reachable
with it.

Agent forwarding solves this: the private key stays in \`ssh-agent\` on the
laptop the whole time. When bastion needs to authenticate onward, it
forwards the cryptographic *challenge* back to the laptop's agent over a
forwarded socket, the laptop signs it, and sends back only the signed
proof — never the key itself.

## Verified Tonight

- SSH'd laptop -> bastion -> private instance successfully, with zero key
  files present on bastion (confirmed no .pem file was needed there)
- Private instance has NO public IP — unreachable directly from internet
- Private instance has NO outbound internet access either — \`curl\` to
  google.com timed out (connection timed out after 5s), confirming no
  route to an Internet Gateway exists for this subnet
- Only path in: through bastion, via the private subnet's internal routing

## CLI Commands Used

\`\`\`bash
# Load private key into local SSH agent (key never leaves laptop)
eval $(ssh-agent -s)
ssh-add ~/ranjay-key.pem

# SSH into bastion WITH agent forwarding enabled (-A flag)
ssh -A -i ~/ranjay-key.pem ubuntu@<bastion-public-ip>

# From inside bastion, SSH onward into private instance
# (no -i flag, no key file present — auth relayed back to laptop)
ssh ubuntu@<private-instance-private-ip>

# Launch the private instance (run BEFORE the SSH steps above)
aws ec2 run-instances \
  --image-id <ami-id> \
  --instance-type t3.micro \
  --key-name ranjay-key \
  --security-group-ids <private-sg-id> \
  --subnet-id subnet-014bc34b50cf94a39 \
  --tag-specifications 'ResourceType=instance,Tags=[{Key=Name,Value=ranjay-private-server}]' \
  --region ap-south-1

# Verify no public IP was assigned
aws ec2 describe-instances \
  --filters "Name=tag:Name,Values=ranjay-private-server" \
  --query 'Reservations[0].Instances[0].[InstanceId,State.Name,PublicIpAddress,PrivateIpAddress]' \
  --output table --region ap-south-1
\`\`\`

## Key Lessons

- Public subnet = has a route to an Internet Gateway. Private subnet = no
  such route, regardless of security group rules.
- A security group only controls WHO can reach an instance — it does not
  grant internet connectivity. Even with a wide-open security group, the
  private instance still can't reach or be reached by the internet, because
  there's no route out at the subnet/route-table level.
- Agent forwarding (-A) is what makes multi-hop SSH secure — without it,
  the only alternative is copying a private key onto a public-facing
  server, which is a real security anti-pattern.
- This bastion + private subnet pattern is exactly how production
  databases and internal services are typically isolated from the public
  internet in real AWS environments.
