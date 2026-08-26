# HomeServer

A self-hosted dashboard for monitoring and controlling a home server — live system stats, one-click Docker container management, and auto-generated links to every hosted app, all behind a single Nginx front door.

![status](https://img.shields.io/badge/status-work--in--progress-yellow)

## Overview

HomeServer turns a headless Linux box into something you'd actually want to look at. Instead of SSH-ing in to check `docker ps` or `htop`, the dashboard gives you:

- **Live system metrics** — CPU and memory usage, refreshed automatically, alongside static info like CPU model, core count, and OS version.
- **Container management** — every running container is listed with its live status, and can be started, stopped, or restarted with a single click.
- **Service launcher** — any container flagged as an "application" (as opposed to core infra like nginx or the API server) automatically gets a card with a one-click link to open it.
- **Reverse-proxied services** — Nginx routes multiple subdomains (dashboard, Jellyfin, etc.) through a single entry point.

## Tech Stack

| Layer | Tech |
|---|---|
| Backend | Java, Spring Boot, [docker-java](https://github.com/docker-java/docker-java), [OSHI](https://github.com/oshi/oshi) |
| Frontend | React, Vite |
| Infra | Docker, Docker Compose, Nginx |
| Hosted apps | Jellyfin, Nextcloud (more added as needed) |

## Architecture

```
                        ┌────────────────┐
                        │      Nginx     │
                        │ (reverse proxy)│
                        └───────┬────────┘
                 ┌──────────────┼───────────────┐
                 │              │               │
         ┌───────▼──────┐ ┌─────▼──────┐ ┌──────▼───────┐
         │   Dashboard   │ │  Spring    │ │   Jellyfin,  │
         │   (React)     │ │  Boot API  │ │   Nextcloud, │
         │               │ │            │ │   ...        │
         └───────────────┘ └─────┬──────┘ └──────────────┘
                                  │
                          ┌───────▼────────┐
                          │  Docker Engine │
                          │  (via socket)  │
                          └────────────────┘
```

The API talks to the Docker Engine to list, inspect, and control containers, and uses OSHI to read live hardware/OS metrics directly from the host. The React dashboard polls both and renders them as cards.

## API

| Endpoint | Method | Description |
|---|---|---|
| `/api/status` | GET | Health check |
| `/api/system/info` | GET | Static server info (CPU model, cores, memory, OS) |
| `/api/system/metrics` | GET | Live CPU / memory usage |
| `/api/docker/getApplications` | GET | List containers flagged as user-facing applications |
| `/api/docker/{id}/start` | POST | Start a container |
| `/api/docker/{id}/stop` | POST | Stop a container |
| `/api/docker/{id}/restart` | POST | Restart a container |

## Getting Started

**Requirements:** Docker & Docker Compose.

```bash
git clone https://github.com/AlpBuz/HomeServer.git
cd HomeServer

# development (hot reload, dummy containers for testing the grid)
docker compose -f docker-compose-dev.yml up

# production
docker compose -f docker-compose.prod.yml up -d
```

The dashboard is served through Nginx once the stack is up. Update `nginx.conf` / `nginx.dev.conf` with your own domains/subdomains for each hosted service.