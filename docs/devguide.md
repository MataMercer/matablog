# How to Build MataBlog (WIP)

## Overview
Matablog is currently not ready for production. This guide is only for development.

## Tools
All tools listed are free to download. Make sure you have all these before proceeding.

### Backend
- IntelliJ 
- Docker
- Git
### Frontend
- VSCode
- NodeJS
- Yarn
- Git

## Run Matablog API

Clone the repository and go into it

`git clone https://github.com/MataMercer/matablog.git`

`cd matablog/microblog`

Run docker compose. This starts the matablog app, database, and cache containers all in one. API will be available
on port 8080.

`docker-compose up`

### Debug
In IntelliJ
- Run -> Edit Configurations...
- In run config, add Remote JVM Debug config by clicking + button.
- In "Before Launch" make sure run Docker Compose is added.

## Configuring Matablog API
### Github Oauth Support

TODO

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