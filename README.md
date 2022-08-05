# TrainsAPI

Simple API designed to improve Spring Boot skills.

## Example usage

Start application with `./gradlew clean bootRun`

Run tests with `./gradlew clean test`

### Extension

Try creating a frontend to access the API (Either assuming it's already initialised or initialising it manually at startup)

## Security

API uses OAuth2 which can be setup using KeyCloak in a docker container if desired.

## Properties

| Property                                                   | Description                                                           | example |
|------------------------------------------------------------|-----------------------------------------------------------------------|---------|
| trainsapi.schedules.downtime                               | Time which trains should be idle between scheduled times              | `PT2m`  |
| trainsapi.initialisation.actor.\[trainCount/stationCount\] | Number of trains/stations to create when running actor initialisation | `100`   |