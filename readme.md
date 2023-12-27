# UniTech Project

Welcome to the UniTech project, a fintech solution for bank products.

## Project Overview

The UniTech project consists of several functionalities. Each functionality is described below along with the definition of done.

## Prerequisites

Before you start, ensure you have the following:

1. **Java 17**: Install Java 17 on your system.
2. **Redis with Docker Compose**: In the `wiki` folder, you can find the Docker Compose file (`docker-compose.yml`) for setting up Redis. Run `docker-compose up -d` in the `wiki` directory to start Redis.

### Database
This project uses the H2 database.

## Running the Project

To run the UniTech project, follow these steps:

1. Clone the repository:

   ```bash
   git clone https://github.com/isakhanli/unitech.git

   cd UniTech
   
   mvn spring-boot:run
   ```


### 1. Register API


#### Endpoint

- **Path**: `/auth/register`
- **Method**: `POST`

#### Request Sample

```json
{
  "pin": "1234",
  "password": "password"
}
```

#### Response Sample

```json
{
  "status": {
    "code": 1000,
    "message": "Success"
  }
}
```


### 2. Login API


#### Endpoint

- **Path**: `/auth/login`
- **Method**: `POST`

#### Request Sample

```json
{
  "pin": "",
  "password": ""
}
```

#### Response Sample

```json
{
  "accessToken": "",
  "status": {
    "code": 0,
    "message": ""
  }
}
```


### 3.  Get accounts API


#### Endpoint

- **Path**: `/accounts`
- **Method**: `GET`

#### Response Sample

```json
{
  "accountList": [
    {
      "id": "",
      "amount": 0.00,
      "currency": "AZN",
      "status": "ACTIVE"
    }
  ],
  "status": {
    "code": 0,
    "message": ""
  }
}
```


### 4. Account to account API


#### Endpoint

- **Path**: `/account/transfer`
- **Method**: `POST`

#### Request Sample

```json
{
  "source": "",
  "target": "",
  "amount": 0.00,
  "currency": "AZN"
}
```

#### Response Sample

```json
{
  "source": {
    "id": "",
    "amount": 0.00,
    "currency": "AZN",
    "status": "ACTIVE"
  },
  "status": {
    "code": 0,
    "message": ""
  }
}
```



### 5. Currency rates API


#### Endpoint

- **Path**: `/currency`
- **Method**: `GET`

#### Response Sample

```json
{
  "rate": 0.00,
  "status": {
    "code": 0,
    "message": ""
  }
}
```