GET http://wiremock:8080/billing-addresses/0815
Accept: application/vnd.de.openknowledge.onlineshop.v1+json

HTTP 200
[Asserts]
jsonpath "$.street.streetName" == "Am Rundtörn"
jsonpath "$.street.houseNumber" == "30"
jsonpath "$.city" == "26135 Oldenburg"

GET http://wiremock:8080/billing-addresses/0815
Accept: application/json

HTTP 200
[Asserts]
jsonpath "$.street.streetName" == "Am Rundtörn"
jsonpath "$.street.houseNumber" == "30"
jsonpath "$.city" == "26135 Oldenburg"
