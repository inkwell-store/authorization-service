# Authorization Server
This repository contains a server that register new users (credentials) and emits JWT tokens.

The current approach uses a custom Authentication system, with a custom JWTService, meaning I'm not using Spring's OAuth2. The reason I choose this approach here it's because it's simpler and requires less configuration. The near future I'll change this approach and, in fact, implement the pattern recommended by OAuth2.1. This update will change the API.

For more information about the whole system check the [main page](https://github.com/inkwell-store).

## Endpoints
The service exposes 3 public endpoints, one to create an account, one to authenticate and get a token, and one to fetch a public key (to be used by Resource Servers).

### Account Creation
* **POST** `/api/auth/create-account`

It requires a payload of format:
```json
{
    "email": "example@gmail.com",
    "password": "password_example"
}
```

### Login
* **POST** `/api/auth/login`

It requires a payload of format:
```json
{
    "email": "example@gmail.com",
    "password": "password_example"
}
```

The response retunrs a payload with format:
```json
{
    "token": "jwt_token_string_representation"
}
```

The JWT token encodes a payload with the following format:
```json
{
    "iss": "http://auth-service:8081",
    "sub": "02f2ad05-5707-4f25-b48f-6d36143011c1",
    "exp": 1786505618,
    "iat": 1786502018,
    "roles": [
        "ROLE_CUSTOMER"
    ]
}
```

Current Roles are as follow: 
* `ROLE_CUSTOMER`
* `ROLE_ADMIN`

### JWKS
* **GET** `/.well-known/jwks.json`

Used by resource servers to validate jwt tokens.

The payload has the format specified on https://datatracker.ietf.org/doc/html/rfc7517#section-4

## About the private key
Authorization and Authentication require the use of a pair of keys: a `public_key` and a `private_key`. Following the OAuth protocol an authorization server is responsible for issuing JWT tokens and a resource server validates them. In this application the pair was created using openssl and a RSA algorithm.

The `private key` remains accessible only within the authorization server, while the `public keys` is used by the resource server. It's not recommended to commit the private_key directly inside the project's source code, much less publish it to a public repository, as it is the case with this one. Although there are workarounds for this issue, in this case, I prefer to push the private_key to the remote repository because the application doesn't hold sensitive data and it's simpler to do this.
