import boto3

s3 = boto3.client("s3")
response = s3.list_buckets()

print("S3 Buckets:")
bucket_count = 0
for bucket in response["Buckets"]:
    print(f"  - {bucket['Name']}")
    bucket_count += 1
print(f"Total buckets: {bucket_count}")

ec2 = boto3.client("ec2")
response = ec2.describe_instances()

print("\nEC2 Instances:")
instance_count = 0
for reservation in response["Reservations"]:
    for instance in reservation["Instances"]:
        instance_id = instance["InstanceId"]
        state = instance["State"]["Name"]
        print(f"  - {instance_id} ({state})")
        instance_count += 1
print(f"Total instances: {instance_count}")
