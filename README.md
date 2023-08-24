# Workshop Consumer-Driven Contract Testing

Herzlich willkommen zur Übung der Verwendung von Pact in asynchronen Umgebungen.

## Übung - Consumer-Driven Contract Tests für Messaging schreiben

Im `customer-service` und `billing-service` liegen bereits Testklassen für Messaging-Contracts.
Beide sind noch unvollständig.

### Consumer-Test (`customer-service`)

Vervollständige den Pact-Consumer-Test in
`customer-service/src/test/java/de/openknowledge/sample/address/domain/BillingAddressRepositoryEventingTest.java`:

Füge hinter `.expectsToReceive("Update for 0815")` einen Message-Body mit `withContent(new PactDslJsonBody()...)` hinzu.
Die Nachricht beschreibt eine Adressaktualisierung für Kundennummer `0815`:
- `number`: `"0815"`
- `billingAddress.recipient`: `"Erika Mustermann"`
- `billingAddress.city`: `"45127 Essen"`
- `billingAddress.street.name`: `"II. Hagen"`
- `billingAddress.street.number`: `"7"`

### Provider-Test (`billing-service`)

Vervollständige den Pact-Provider-Test in
`billing-service/src/test/java/de/openknowledge/sample/address/BillingAddressServiceMessagingTest.java`:

Ersetze im `TODO`-Kommentar die fehlende HTTP-Anfrage, die das SSE-Event auslöst.
Führe einen `POST`-Request an `/billing-addresses/0815` mit dem Inhalt der Datei `0816.json`
(liegt unter `src/test/resources/de/openknowledge/sample/address/`) und dem Content-Type `application/json` aus.
Entferne außerdem den Timeout aus dem `futureEvent.get()`-Aufruf.

Tipp: Verwende `entity(getClass().getResourceAsStream("0816.json"), APPLICATION_JSON_TYPE)` als Request-Body
und importiere die benötigten statischen Methoden aus `jakarta.ws.rs.client.Entity` und `jakarta.ws.rs.core.MediaType`.

## Ziel

Messaging Consumer- und Providertest laufen erfolgreich durch.
