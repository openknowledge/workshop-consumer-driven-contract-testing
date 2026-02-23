# Workshop API Design

Herzlich willkommen zum Workshop API Design.

## Übung: Provider States verwenden

### Ausgangssituation

Zwei Tests zum Ändern von Adressen schlagen fehl.

### Aufgabe

Analysiere das Problem und behebe es so, dass sowohl die Consumer-Tests
als auch die anschließende Provider-Verifikation erfolgreich ist.

### Ziel

Nach der Implementierung verifiziert `CustomerServiceTest.java`
den entstandenen Contract erfolgreich.

## Pact Provider States

Einige Interaktionen im Contract setzen einen bestimmten Zustand des Providers voraus,
der im Consumer-Test über .given(...) angegeben werden kann.

Der Customer Service bietet folgende Zustände (Provider States) an:

### `James has billing address`

Wird für `GET /customers/007/billing-address` verwendet, nachdem eine Rechnungsadresse für Kunde 007 (James Bond) angelegt wurde.

Der Provider muss in diesem State folgende Rechnungsadresse zurückgeben:

```json
{
  "recipient": "James Bond",
  "street": {
    "name": "Albert Embankment",
    "number": "85"
  },
  "city": "SE1 7TP London"
}
```

### `James has delivery address`

Wird für `GET /customers/007/delivery-address` verwendet, nachdem eine Lieferadresse für Kunde 007 (James Bond) angelegt wurde.

Der Provider muss in diesem State folgende Lieferadresse zurückgeben:

```json
{
  "recipient": "James Bond",
  "street": {
    "name": "Chausseestraße",
    "number": "96 - 99a"
  },
  "city": "10115 Berlin Mitte"
}
```

