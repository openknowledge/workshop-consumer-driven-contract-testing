# Workshop API Design

Herzlich willkommen zum Workshop API Design.

## Übung: Mehrere Versionen zur Verfügung stellen

### Ausgangssituation

Im Ordner `backward-compatibility` befinded sich eine `docker-compose.yaml`.

Der Mock-Server kann unter [Wiremock](http://localhost:7070) aufgerufen werden.
Er kann über die [Swagger UI](http://localhost:6060) bedient werden.

Getestet werden kann die API über [Hurl](https://hurl.dev/) (siehe Ordner `hurl`).

### Aufgabe

Konfiguriere den Wiremock-Server so, dass er die Version 1.0, 1.1 und 2.0
des Billing Services zur Verfügung stellt.

#### Hinweis

Nach jeder Änderung muss der Server neu gestartet werden.

### Ziel

Alle Hurl-Tests sind (gleichzeitig) erfolgreich.
