# Dockerized Java App

A minimal Java app packaged into a Docker image.

## Concept
- **Image** = static package. **Container** = running instance of it (class vs object).
- `RUN` runs at build time and bakes into the image. `CMD` runs at container startup.

## Files
- `Main.java` — prints two lines
- `Dockerfile` — build instructions

## Run it
\`\`\`bash
docker build -t my-java-app .   # build image from Dockerfile
docker run my-java-app          # run container from image
\`\`\`
