# Authorization Server

This repository contains an Authorization Server that emits JWT tokens following OAuth2 protocol.

For more information about the whole system check the (page)[https://github.com/inkwell-store]

## Endpoints
The service exposes 2 public endpoints:

* POST `/api/auth/create-account`: creates a new set of credentials 
* POST `/api/auth/login`:

Both of them require the following payload:

```json
{
    "email": "example@gmail.com",
    "password": "password_example"
}
```

## JWT Payload Structure

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

## About the private key
Authorization and Authentication require the use of a pair of keys: a `public_key` and a `private_key`. Following the OAuth protocol an authorization server is responsible for issuing JWT tokens and a resource server validates them. In this application the pair was created using openssl and a RSA algorithm.

The `private key` remains accessible only within the authorization server, while the `public keys` is used by the resource server. It's not recommended to commit the private_key directly inside the project's source code, much less publish it to a public repository, as it is the case with this one. Although there are workarounds for this issue, in this case, I prefer to push the private_key to the remote repository because the application doesn't hold sensitive data and it's simpler to do this.