import boto3

ec2 = boto3.client("ec2")


def get_instance_state(instance_id):
    response = ec2.describe_instances(InstanceIds=[instance_id])
    instance = response["Reservations"][0]["Instances"][0]
    return instance["State"]["Name"]


def stop_instance(instance_id):
    state = get_instance_state(instance_id)
    if state == "running":
        print(f"Instance {instance_id} is already running. No action taken.")
        return
    print(f"Starting instance {instance_id}...")
    ec2.start_instances(InstanceIds=[instance_id])
    print("Start command sent.")


def stop_instance(instance_id):
    state = get_instance_state(instance_id)
    if state == "stopped":
        print(f"Instance {instance_id} is already stopped. No action taken.")
        return
    print(f"Stopping instance {instance_id}...")
    ec2.stop_instances(InstanceIds=[instance_id])
    print("Stop command sent.")


instance_id = "i-0f9e2d8253ee91596"  # your private instance

state = get_instance_state(instance_id)
print(f"Instance {instance_id} is currently: {state}")

stop_instance(instance_id)
