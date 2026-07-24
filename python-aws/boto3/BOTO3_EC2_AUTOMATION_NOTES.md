# boto3 EC2 Start/Stop Automation — Quick Reference

Built Week 6, Day 4. Covers checking instance state before acting, and
starting/stopping an EC2 instance via boto3.

---

## Core Principle: Check State Before Acting

Never blindly fire `start_instances` or `stop_instances` without first
checking the instance's current state. AWS won't necessarily crash if
you call start on an already-running instance, but a real automation
script should still check first — it avoids wasted API calls, avoids
unclear logs, and avoids sending commands during an already in-progress
transition.

---

## Getting a Single Instance's State

```python
def get_instance_state(instance_id):
    response = ec2.describe_instances(InstanceIds=[instance_id])
    instance = response["Reservations"][0]["Instances"][0]
    return instance["State"]["Name"]
```

`InstanceIds=[instance_id]` — always a list, even for one ID, since the
underlying API supports checking multiple instances in a single call.

Since we're asking about exactly one instance, it's safe to index
`[0]` directly into `Reservations` and `Instances` rather than looping,
unlike a "list everything" script.

---

## Start / Stop With State Check

```python
def start_instance(instance_id):
    state = get_instance_state(instance_id)
    if state == "running":
        print("Already running. No action taken.")
        return
    ec2.start_instances(InstanceIds=[instance_id])

def stop_instance(instance_id):
    state = get_instance_state(instance_id)
    if state == "stopped":
        print("Already stopped. No action taken.")
        return
    ec2.stop_instances(InstanceIds=[instance_id])
```

---

## Real EC2 Instance States (not just running/stopped)

An instance moves through intermediate states, not just the two you'd
naively expect:

pending -> running -> stopping -> stopped
-> shutting-down -> terminated


**Bug hit tonight:** checking `if state == "stopped"` right after
issuing a stop command caught the instance mid-transition, in the
`"stopping"` state — which does NOT equal `"stopped"`. The check missed
this and fired a redundant stop command. A more production-grade script
would either poll in a loop until the state settles, or explicitly
handle in-progress states rather than only checking the final ones.

---

## Bugs Hit Building This Script (real lessons, not hypothetical)

1. **Defined a function but never called it.** Wrote `start_instance()`
   as a function, ran the script, and nothing happened — because
   `def` only defines a function, it doesn't run the code inside until
   something calls it by name elsewhere in the script. Classic mistake,
   easy to miss since the script runs with no errors at all.

2. **Shell `echo` command text ended up inside the Python file** when
   trying to append a line via a multi-line `echo '...' >> file`
   command — the shell's own command syntax got written into the file
   as literal text instead of just its output. Fixed by rewriting the
   file cleanly instead of patching around it.

3. **State transitions aren't instant.** `stopping` is a real,
   distinct state from `stopped` — checking too early after issuing a
   stop command will catch the in-between state.
