# PW coins

## Prerequisites

1. Create PostgreSql database with the name 'coins' and 'coins_test'
   ```txt
   # psql
   CREATE DATABASE coins;
   ```

## Configurations

1. Setup [properties](src/main/resources/application.yml)
2. Setup env variables for gradle tasks.

   ```txt
   DATABASE_PASSWORD=root;
   DATABASE_URL=postgresql://localhost:5432/coins;
   DATABASE_USERNAME=akashkou;
   TEAMS_BOT_ENDPOINT=http://localhost:9001;
   USOS_API_SECRET=;
   USOS_API_KEY=;
   JWT_SECRET=;
   ```

3. Let intellij build with intellij, not with gradle

## Some info

1. It's used Jooq and jooq generator for the database layer
