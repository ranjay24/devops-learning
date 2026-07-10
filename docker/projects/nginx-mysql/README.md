# Nginx + MySQL Docker Compose Project

## What This Project Does
Runs nginx and MySQL as separate containers on a shared custom network.

## Architecture
- **nginx** — web server accessible at http://localhost:8080
- **mysql** — database with persistent volume storage
- **ranjay-network** — custom bridge network connecting both containers
- **mysql-data** — named volume for MySQL data persistence

## How To Run

```bash
docker-compose up -d
```

## How To Stop

```bash
docker-compose down
```

## How To Stop And Remove Volume

```bash
docker-compose down -v
```

## Verify Running Containers

```bash
docker ps
```

## Check Network

```bash
docker network inspect nginx-mysql_ranjay-network
```