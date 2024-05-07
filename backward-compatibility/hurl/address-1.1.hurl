GET http://wiremock:8080/billing-addresses/0815
Accept: application/json

HTTP 200
[Asserts]
jsonpath "$.addressLine1" == "Am Rundtörn 30"
jsonpath "$.addressLine2" == ""
jsonpath "$.location.zipCode" == "26135"
jsonpath "$.location.cityName" == "Oldenburg"

GET http://wiremock:8080/billing-addresses/0815
Accept: application/vnd.de.openknowledge.onlineshop.v1+json

HTTP 200
[Asserts]
jsonpath "$.addressLine1" == "Am Rundtörn 30"
jsonpath "$.addressLine2" == ""
jsonpath "$.location.zipCode" == "26135"
jsonpath "$.location.cityName" == "Oldenburg"
