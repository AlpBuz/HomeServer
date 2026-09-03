# HomeServer

A self-hosted dashboard for monitoring and controlling a home server — live system stats, one-click Docker container management, and auto-generated links to every hosted app, all behind a single Nginx front door.

![status](https://img.shields.io/badge/status-work--in--progress-yellow)

## Overview

HomeServer turns a headless Linux box into something you'd actually want to look at. Instead of SSH-ing in to check `docker ps` or `htop`, the dashboard gives you:

- **Live system metrics** — CPU and memory usage, refreshed automatically, alongside static info like CPU model, core count, and OS version.
- **Container management** — every running container is listed with its live status, and can be started, stopped, or restarted with a single click.
- **Service launcher** — any container flagged as an "application" (as opposed to core infra like nginx or the API server) automatically gets a card with a one-click link to open it.
- **Email alerts** — get emailed automatically when a container goes down, and again when it recovers.
- **Reverse-proxied services** — Nginx routes multiple subdomains (dashboard, Jellyfin, etc.) through a single entry point.

## Tech Stack

| Layer | Tech |
|---|---|
| Backend | Java, Spring Boot, [docker-java](https://github.com/docker-java/docker-java), [OSHI](https://github.com/oshi/oshi), Spring Mail |
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
| `/api/alert/email` | GET | Whether an alert email is configured, and a masked preview (e.g. `a***@gmail.com`) |
| `/api/alert/email` | POST | Set the alert email — JSON body `{ "email": "you@example.com" }` |
| `/api/alert/email` | DELETE | Remove the configured alert email |

## Getting Started

**Requirements:** Docker & Docker Compose.

```bash
git clone https://github.com/AlpBuz/HomeServer.git
cd HomeServer

# development (hot reload, dummy containers for testing the grid)
docker compose -f docker-compose-dev.yml up

# production
docker compose up -d
```

The dashboard is served through Nginx once the stack is up. Update `nginx.conf` / `nginx.dev.conf` with your own domains/subdomains for each hosted service.

## Setting Up Email Alerts

When a container goes down, the server emails you about it, and emails you again once it comes back up. Two independent pieces make this work: **who** gets notified, and the **account alerts get sent from**.

### Who gets notified

Set from the dashboard — click the envelope icon next to the Dark/Light toggle in the header, enter an address, and save. It's persisted server-side to `server/data/alert-owner-email.txt` (gitignored, mounted as a Docker volume so it survives container restarts) and validated before being stored. The API never hands the full address back, only a masked preview like `a***@gmail.com`.

You can also manage it directly, e.g. for scripting:

```bash
# set it
curl -X POST http://localhost/api/alert/email \
  -H "Content-Type: application/json" \
  -d '{"email":"you@example.com"}'

# check status
curl http://localhost/api/alert/email

# remove it
curl -X DELETE http://localhost/api/alert/email
```

### The account alerts get sent from

This is a separate, one-time setup step: the SMTP account the server logs into to actually send mail. It's configured via a `server/.env` file, which is gitignored and should never be committed:

```bash
cp server/.env.example server/.env
# then edit server/.env with real values
```

```
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=youraddress@gmail.com
MAIL_PASSWORD=your-app-password
MAIL_FROM=youraddress@gmail.com
```

After editing `server/.env`, recreate the `server` container so it picks up the new environment variables (a plain restart won't reload a newly-created `.env` file):

```bash
docker compose up -d server
```

#### Gmail

- `MAIL_HOST=smtp.gmail.com`, `MAIL_PORT=587`
- Requires 2-Step Verification to be turned on for the account, then an [App Password](https://myaccount.google.com/apppasswords) generated specifically for this — your regular Gmail password will **not** work for SMTP.
- `MAIL_USERNAME` and `MAIL_FROM` should both be the full `@gmail.com` address.

#### Outlook / Hotmail / Live (personal accounts)

- `MAIL_HOST=smtp-mail.outlook.com`, `MAIL_PORT=587`
- If the account has 2-step verification on (recommended), generate an [App Password](https://account.live.com/proofs/AppPassword) and use that for `MAIL_PASSWORD` instead of the normal login password.
- Microsoft has been phasing out plain username/password ("Basic Auth") SMTP login on personal accounts in favor of OAuth2, which this project doesn't implement. If authentication fails even with a fresh app password, that account has likely lost Basic Auth support — use a Gmail account or a dedicated transactional email provider instead.

#### Office 365 / work or school accounts

- `MAIL_HOST=smtp.office365.com`, `MAIL_PORT=587`
- Most organizations disable Basic Auth for SMTP entirely, so this often won't work on a managed account regardless of password/app-password — check with your admin, or use a personal account instead.

#### Any other SMTP provider

Any standard SMTP server works — just point `MAIL_HOST`/`MAIL_PORT` at it (Mailgun, SendGrid, Fastmail, a self-hosted relay, etc.). The defaults assume port `587` with STARTTLS; if your provider needs implicit TLS on port `465` or something else non-standard, adjust the `spring.mail.*` properties in `server/src/main/resources/application.properties`.

### Avoiding alert spam

If a container stays down, it won't re-trigger an email on every check — by default, alerts for the same container repeat at most every 30 minutes. Override with `ALERT_RENOTIFY_MINUTES` in `server/.env`.