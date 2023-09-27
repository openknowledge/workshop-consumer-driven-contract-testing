# cdc-customer-service

Customer Service to show Consumer-Driven Contracts

# diff two openapi-specs

## backwards compatible (compares v1.0 with v1.1)
```shell
docker run --rm -t -v $(pwd)/src/main/resources/META-INF:/specs:ro openapitools/openapi-diff:2.0.1 /specs/v1.0.yaml /specs/v1.1.yaml
```

## in reverse -> not backwards compatible (compares v1.1 with v1.0)
```shell
docker run --rm -t -v $(pwd)/src/main/resources/META-INF:/specs:ro openapitools/openapi-diff:2.0.1 /specs/v1.1.yaml /specs/v1.0.yaml
```

## not backwards compatible (compares v1.1 with v2.0)
```shell
docker run --rm -t -v $(pwd)/src/main/resources/META-INF:/specs:ro openapitools/openapi-diff:2.0.1 /specs/v1.1.yaml /specs/openapi.yaml
```