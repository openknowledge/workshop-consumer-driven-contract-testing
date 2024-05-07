GET http://wiremock:8080/billing-addresses/0815
Accept: application/vnd.de.openknowledge.onlineshop.v2+json

HTTP 200
[Asserts]
jsonpath "$.addressLine1" == "Am Rundtörn 30"
jsonpath "$.addressLine2" == ""
jsonpath "$.location.zipCode" == "26135"
jsonpath "$.location.cityName" == "Oldenburg"
jsonpath "$.street" not exists
jsonpath "$.street.streetName" not exists
jsonpath "$.street.houseNumber" not exists
jsonpath "$.city" not exists
