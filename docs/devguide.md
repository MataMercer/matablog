# How to Build MataBlog (WIP)

## Overview
Matablog is currently not ready for production. This guide is only for development.

## Tools
All tools listed are free and open source. Make sure you have all these before proceeding. The guide assumes you are
using these tools, especially IntelliJ.

### Backend
- IntelliJ 
- Docker
- Git
### Frontend
- VSCode
- NodeJS
- Yarn
- Git

## Run Matablog API (Option A, Preferred)

Clone the repository and go into it

`git clone https://github.com/MataMercer/matablog.git`

`cd matablog/microblog`

This method runs the API without Docker. However, we first need a database running. The easiest way is through docker.
Open up `docker-compose-db-only.yml` and click the green play button next to services.

For the API itself, run it how you normally run Java/Kotlin applications. Navigate to the main file, `MatablogApplication.kt`. Click the green play button in the left gutter.
Make sure the database (PostGres) is running. The API will be available on port 8080.


## Run Matablog API inside Docker Compose (Option B)

Clone the repository and go into it

`git clone https://github.com/MataMercer/matablog.git`

`cd matablog/microblog`

Run docker compose. This starts the matablog app, database, and cache containers all in one. API will be available
on port 8080.

`docker-compose up --build`

## Configuring Matablog API
### Github Oauth Support
The API follows the auth code flow method with the front end app (localhost:3000) in mind. The config is only available if you use option A to run the API.
- On Github's website, go to your Settings -> Developer Settings -> New Oauth App. Follow the prompts.
- Make you set homepage URL to `http://localhost:3000/` and Authorization Callback URL to `http://localhost:3000/oauth/callback`.
- Copy both the client ID and client secret.
- Go into IntelliJ and edit your run config. Set `GITHUB_CLIENT_ID` and `GITHUB_CLIENT_SECRET` to the 
values you copied respectively.
- Log in via Github on the front end app at localhost:3000 with the API running on localhost:8080 and it should work.
## Run Matablog Frontend
Download the front end web app repository and go into it.

`git clone https://github.com/MataMercer/matablog-frontend.git`

`cd matablog-frontend`

Install dependencies

`yarn install`

Start the client

`yarn dev`

Go to `localhost:3000` in your browser to see the web page. 

## Questions / Concerns

Create an issue or a discussion in Matablog repository if you have any trouble. 