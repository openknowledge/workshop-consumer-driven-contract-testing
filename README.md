# Workshop Consumer-Driven Contract Testing

Herzlich willkommen zur Übung der Verwendung von Pact in einem Java-Consumer.

## Übung: Consumer-Driven Contract Tests schreiben

Im `customer-service` liegt bereits eine Testklasse `DeliveryAddressRepositoryTest`,
die Consumer-Driven Contract Tests mit Pact für den Delivery Service definiert.
Die Tests sind jedoch noch unvollständig: Requests und Responses enthalten noch keine Bodies,
und ein Statuscode ist falsch gesetzt.

Vervollständige die Pact-Consumer-Tests in der Klasse
`customer-service/src/test/java/de/openknowledge/sample/address/domain/DeliveryAddressRepositoryTest.java`:

1. **`getMax`** – Füge einen Response-Body hinzu, der die Lieferadresse von Max Mustermann beschreibt:
   - `recipient`: `"Max Mustermann"`
   - `city`: `"26122 Oldenburg"`
   - `street.name`: `"Poststr."`
   - `street.number`: `"1"`

2. **`dontGetMissing`** – Korrigiere den Statuscode: Eine nicht gefundene Adresse sollte `404` (nicht `204`) zurückliefern.

3. **`updateMax`** – Füge einen Request-Body hinzu, der die neue Lieferadresse beschreibt:
   - `recipient`: `"Erika Mustermann"`
   - `city`: `"45127 Essen"`
   - `street.name`: `"II. Hagen"`
   - `street.number`: `"7"`

4. **`dontUpdateSherlock`** – Vervollständige Request und Response:
   - Füge einen `Content-Type: application/json`-Header-Match für den Request hinzu.
   - Füge einen Request-Body mit der Adresse von Sherlock Holmes hinzu:
     - `recipient`: `"Sherlock Holmes"`
     - `city`: `"London NW1 6XE"`
     - `street.name`: `"Baker Street"`
     - `street.number`: `"221B"`
   - Füge einen `Content-Type: application/problem+json`-Header-Match für die Response hinzu.
   - Füge einen Response-Body hinzu, der ein `detail`-Feld mit einer Fehlermeldung enthält
     (Matcher erlaubt beliebigen Text, Beispiel: `"Addresses from UK are not supported for delivery"`).

Tipp: Verwende `PactDslJsonBody` (bereits als Abhängigkeit vorhanden) zum Aufbau der Bodies.

Wenn die Tests erfolgreich durchlaufen, werden die Pact-Dateien unter `target/pacts/` erzeugt.
