# How To Use

## Production
Have docker-compose on your computer. I discourage running this on Windows 10 Home unless you have a powerful computer. It is VERY
slow due to running the app on the WSL 2 Virtual Machine. A Linux machine is recommended for this. 

Clone the repository.

`cd microblog`

`docker-compose up`

Docker should run everything automatically after it builds microblog. Note: currently I haven't 
configured Profiles properly yet. You must change the postgres and redis IPs to 127.0.0.1.

## Development (Build + Run Microblog natively)

Run Postgres in Docker

`docker run --name postgres-desktop-microblog -e POSTGRES_PASSWORD=password -e POSTGRES_USER=postgres -e POSTGRES_DB=microblog -p 5432:5432 -d postgres:alpine`

Run Redis in Docker

`docker run --name redis-desktop-microblog -p 6379:6379 -d redis:alpine`

Git clone the repository to your PC.
Run yarn start in the resources folder to build react code. 

Open the project in Intellij or VSCode and build with maven and run it.
