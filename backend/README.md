# PW coins

## Prerequisites

1. Create PostgreSql database with the name 'coins'

## Configurations

1. Setup [properties](src/main/resources/application.properties)

## Some info

1. It's used Jooq and jooq generator for the database layer

## Conventions

1. These suffixes are used in the namings: repositories (Re), services (Se), controllers (Co). Ex. classes can be named
   like: UserRe, UserSe, UserCo
2. There are 2 user models. One is User, second is TeamsUser. User is the main model that simply represents a single
   user in the "coins" system, usually it's called as an "origin" user for the MS Teams user.