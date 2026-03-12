# Workshop API Design

Herzlich willkommen zum Workshop API Design.

## Übung: Pact Matchers

### Ausgangssituation

Im Branch `pact-matchers` ist der Consumer-Test `create-customer.spec.ts` bereits mit Pact-Interaktionen ausgestattet. Der Test enthält jedoch zwei hartcodierte Werte, die den Contract unnötig starr machen:

1. Der `Location`-Header der POST-Antwort enthält eine fest eingetragene URL mit Port und Kundennummer:
   ```typescript
   Location: 'http://localhost/customers/1'
   ```
   Die Kundennummer wird vom Service automatisch vergeben und ist nicht vorhersehbar.

2. In der Kundenliste ist die Kundennummer von Sherlock Holmes ebenfalls hartcodiert:
   ```typescript
   { number: '1', name: 'Sherlock Holmes' }
   ```

Führt man die Tests aus, schlagen sie fehl, weil die tatsächlichen Werte nicht mit den hartcodierten Erwartungen übereinstimmen.

### Aufgabe

Ersetze die hartcodierten Werte durch passende Pact Matchers aus `MatchersV3` (Import: `import { MatchersV3 } from '@pact-foundation/pact'`).

1. **`Location`-Header** – der Wert soll dem Muster `http://localhost/customers/<Zahl>` entsprechen. Verwende einen Regex-Matcher:

   ```typescript
   Location: MatchersV3.regex(
     'http://localhost/customers/\\d+',
     'http://localhost/customers/1',
   )
   ```

2. **Kundennummer in der Liste** – der Wert soll vom richtigen Typ sein, aber nicht exakt übereinstimmen müssen. Verwende einen Type-Matcher:

   ```typescript
   { number: MatchersV3.like('1'), name: 'Sherlock Holmes' }
   ```

### Ziel

Nach der Umsetzung:

- Laufen alle Tests erfolgreich, unabhängig vom zufällig gewählten Port des Mock-Servers und der vom Service vergebenen Kundennummer.
- Der erzeugte Pact dokumentiert die Einschränkungen flexibel: Der `Location`-Header muss dem Regex-Muster entsprechen, die Kundennummer muss ein String sein – nicht aber einen bestimmten Wert haben.
- Der Provider-Test `CustomerServiceTest.java` verifiziert den Contract erfolgreich gegen die echte Implementierung.

