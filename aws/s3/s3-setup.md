# AWS S3 Deep Dive — Day 4 (Wednesday, Week 5)

## What Was Built
S3 bucket with versioning enabled, public read-only bucket policy, and static
website hosting — all via AWS CLI from WSL, no console.

## Core Concepts

**Bucket** — top-level container for objects. Name must be globally unique
across ALL AWS accounts worldwide, not just your own account.

**Object** — the actual file stored inside a bucket (image, video, HTML,
JSON, zip, anything).

**Key** — the object's full name/path inside the bucket, e.g.
`images/logo.png`. S3 has no real folders — it fakes them using keys with
slashes.

**Durability** — S3 offers 99.999999999% (11 nines) durability. AWS
replicates your data across multiple facilities automatically.

**Versioning** — once enabled, S3 never truly overwrites or deletes an
object. Every write creates a new version; old versions remain recoverable
by their VersionId. Cannot be fully removed once enabled — only Suspended.

**Block Public Access** — a safety layer that sits OUTSIDE the bucket
policy. Even a bucket policy that grants public access will be rejected
until Block Public Access is explicitly disabled on that bucket. New
buckets have this ON by default (all 4 sub-settings true).

**Bucket Policy** — a JSON document attached to the bucket controlling who
(Principal) can do what (Action) on which objects (Resource). Same
structure family as IAM policies (Effect / Principal / Action / Resource).

**Static Website Hosting** — lets a bucket serve an index document
automatically at its root URL, plus an optional custom error document.
Produces a distinct website endpoint URL (HTTP only, not HTTPS — CloudFront
is needed in front of S3 for HTTPS in production).

---

## Resources From Tonight

| Resource | Details |
|----------|---------|
| Bucket | ranjay-devops-learning-bucket |
| Region | ap-south-1 |
| Versioning | Enabled |
| Bucket Policy | Public s3:GetObject only (read-only, no write/delete) |
| Block Public Access | Disabled (deliberately, scoped to this bucket only) |
| Website Endpoint | http://ranjay-devops-learning-bucket.s3-website.ap-south-1.amazonaws.com |

---

## Full Command Reference

### 1. Bucket Creation
(Not run tonight — reused an existing bucket. Kept here for reference since
every future S3 task starts here.)

\`\`\`bash
# ap-south-1 (Mumbai) requires an explicit LocationConstraint —
# us-east-1 is the only region that does NOT need this flag
aws s3api create-bucket \
  --bucket <globally-unique-bucket-name> \
  --region ap-south-1 \
  --create-bucket-configuration LocationConstraint=ap-south-1
\`\`\`

### 2. List Buckets / Check Contents

\`\`\`bash
# List all buckets in the account
aws s3 ls

# List contents of a specific bucket (recursive = include nested keys)
aws s3 ls s3://ranjay-devops-learning-bucket --recursive

# Confirm which region a bucket lives in
aws s3api get-bucket-location --bucket ranjay-devops-learning-bucket
\`\`\`

### 3. Upload / Download Objects

\`\`\`bash
# Upload a file (cp = copy, same idea as Linux cp)
aws s3 cp ~/index.html s3://ranjay-devops-learning-bucket/index.html

# Download a file back
aws s3 cp s3://ranjay-devops-learning-bucket/index.html ~/downloaded.html

# Upload an entire folder
aws s3 cp ~/my-folder s3://ranjay-devops-learning-bucket/my-folder --recursive
\`\`\`

### 4. Versioning

\`\`\`bash
# Enable versioning
aws s3api put-bucket-versioning \
  --bucket ranjay-devops-learning-bucket \
  --versioning-configuration Status=Enabled

# Check current versioning status
aws s3api get-bucket-versioning --bucket ranjay-devops-learning-bucket

# List every version of every object (or filter with --prefix <key>)
aws s3api list-object-versions --bucket ranjay-devops-learning-bucket
\`\`\`

### 5. Block Public Access

\`\`\`bash
# Check current settings
aws s3api get-public-access-block --bucket ranjay-devops-learning-bucket

# Disable all 4 sub-settings (required before a public bucket policy works)
aws s3api put-public-access-block \
  --bucket ranjay-devops-learning-bucket \
  --public-access-block-configuration \
  BlockPublicAcls=false,IgnorePublicAcls=false,BlockPublicPolicy=false,RestrictPublicBuckets=false
\`\`\`

### 6. Bucket Policy

Policy file used tonight (\`bucket-policy.json\`):

\`\`\`json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "PublicReadGetObject",
      "Effect": "Allow",
      "Principal": "*",
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::ranjay-devops-learning-bucket/*"
    }
  ]
}
\`\`\`

\`\`\`bash
# Apply the policy
aws s3api put-bucket-policy \
  --bucket ranjay-devops-learning-bucket \
  --policy file://bucket-policy.json

# View current policy
aws s3api get-bucket-policy --bucket ranjay-devops-learning-bucket

# Remove a policy entirely
aws s3api delete-bucket-policy --bucket ranjay-devops-learning-bucket
\`\`\`

### 7. Static Website Hosting

\`\`\`bash
# Enable, pointing to index.html as the default document
aws s3 website s3://ranjay-devops-learning-bucket --index-document index.html

# (Optional) also set a custom error page
aws s3 website s3://ranjay-devops-learning-bucket \
  --index-document index.html --error-document error.html

# Check current website config
aws s3api get-bucket-website --bucket ranjay-devops-learning-bucket

# Disable website hosting
aws s3api delete-bucket-website --bucket ranjay-devops-learning-bucket
\`\`\`

Website endpoint URL pattern (region-specific):
\`\`\`
http://<bucket-name>.s3-website.<region>.amazonaws.com
\`\`\`

Direct object URL pattern (works regardless of website hosting, if the
object itself is publicly readable):
\`\`\`
https://<bucket-name>.s3.<region>.amazonaws.com/<key>
\`\`\`

### 8. Cleanup (for reference — not run tonight)

\`\`\`bash
# Delete a single object
aws s3 rm s3://ranjay-devops-learning-bucket/test-file.txt

# Empty an entire bucket
aws s3 rm s3://ranjay-devops-learning-bucket --recursive

# Delete the bucket itself (must be empty first)
aws s3api delete-bucket --bucket ranjay-devops-learning-bucket --region ap-south-1
\`\`\`

---

## Key Lessons From Tonight

- Bucket names are globally unique across all AWS customers, not just your
  own account — plan naming accordingly.
- Versioning does not prevent overwrites — it preserves every prior version
  so nothing is ever silently lost. Confirmed by uploading a 2nd version of
  test-file.txt and seeing both VersionIds listed.
- Block Public Access is a separate, higher-priority safety layer outside
  the bucket policy itself. A "public" bucket policy will be rejected with
  AccessDenied until all 4 Block Public Access settings are explicitly
  disabled — this is what caused tonight's first error.
- Bucket policies follow the same Effect/Principal/Action/Resource
  structure as IAM policies — worth remembering since IAM comes up
  constantly in AWS work.
- Static website endpoints are HTTP only. HTTPS requires CloudFront in
  front of the bucket — out of scope for tonight, relevant for later
  production-style setups.
- \`aws s3\` commands (cp, ls, website) are simpler, high-level operations.
  \`aws s3api\` commands are lower-level and expose settings (versioning,
  policies, public access block) not available through plain \`aws s3\`.
