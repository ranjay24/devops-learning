# boto3 Basics — Quick Reference

Built Week 6, Day 3. Covers connecting to AWS from Python, listing S3
buckets and EC2 instances.

---

## What Is boto3

Python's SDK (Software Development Kit) for talking to AWS. Same idea as
the `aws` CLI, except your Python code makes the calls instead of you
typing commands manually. Uses the SAME credentials already configured
for the CLI (`~/.aws/credentials`, `~/.aws/config`) — no separate setup,
same IAM user, same permissions.

---

## Basic Pattern

Every AWS service in boto3 follows the same shape:

```python
import boto3

client = boto3.client("<service-name>")   # e.g. "s3", "ec2", "rds"
response = client.<some_method>()          # the actual API call
# response is always a Python dictionary
```

---

## S3 — List Buckets

```python
s3 = boto3.client("s3")
response = s3.list_buckets()

for bucket in response["Buckets"]:
    print(bucket["Name"])
```

Equivalent to `aws s3 ls`. The bucket list lives under the `"Buckets"`
key of the response dict; each bucket is itself a dict with a `"Name"`
key (plus other metadata like creation date, not used yet).

---

## EC2 — List Instances

```python
ec2 = boto3.client("ec2")
response = ec2.describe_instances()

for reservation in response["Reservations"]:
    for instance in reservation["Instances"]:
        instance_id = instance["InstanceId"]
        state = instance["State"]["Name"]
        print(f"{instance_id} - {state}")
```

Equivalent to `aws ec2 describe-instances`.

**Key gotcha — EC2 responses are nested TWO levels deep**, unlike S3's
flat `Buckets` list:
- `Reservations` — one entry per launch event (a "reservation" is
  created every time you run `run-instances`, even for a single
  instance)
- Each reservation has `Instances` — the actual instance(s) launched in
  that batch

So you always need a nested loop for EC2 instances, not a single loop
like S3 buckets.

`instance["State"]` is itself a nested dict (`{"Code": ..., "Name":
...}`) — go one level deeper with `["Name"]` to get the human-readable
state like `"running"` or `"stopped"`.

---

## Quick Gotchas Log

- `pip3` wasn't installed by default on this WSL setup — needed
  `sudo apt install -y python3-pip` first, before `pip3 install boto3`
  would work at all.
- `--break-system-packages` is required on newer pip versions to install
  packages outside a virtual environment on Ubuntu — safe for a learning
  setup, not something you'd necessarily do on a shared production
  machine.
- boto3 responses are always dictionaries — check the actual key names
  (`"Buckets"`, `"Reservations"`, `"Instances"`, `"State"`) rather than
  guessing, since they don't always match the CLI's exact output
  structure.
