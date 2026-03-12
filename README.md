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

---

## Übung: Pact States

### Ausgangssituation

Im Branch `playwright-pact-states` ist der Consumer-Test `create-customer.spec.ts` bereits auf Pact umgebaut. Der Test simuliert folgendes Szenario:

1. Ein neuer Kunde (Sherlock Holmes) wird per POST angelegt.
2. Danach wird die Kundenliste per GET abgerufen – und Sherlock Holmes soll darin erscheinen.

Dabei gibt es zwei Probleme:

- Der Test erstellt den `PactV4`-Provider noch manuell mit `new PactV4(...)` und einem eigenen Consumer-Namen, statt die gemeinsame Hilfsfunktion `createProvider()` aus `pact-proxy.ts` zu nutzen.
- Die GET-Interaction deklariert keine Vorbedingung: Der Provider-Test (`CustomerServiceTest.java`) weiß nicht, dass Sherlock Holmes für diese Verifikation bereits in der Datenbank vorhanden sein muss.
- Der `TestCustomerRepository` setzt seinen Zustand zwischen den einzelnen Pact-Interaktionen nicht zurück, sodass Daten aus einer Interaktion in die nächste „auslaufen" können.

### Aufgabe

#### Consumer-Seite (`create-customer.spec.ts`)

1. Ersetze `import { setupApiProxy } from './pact-proxy'` durch `import { createProvider, setupApiProxy } from './pact-proxy'`.
2. Ersetze die manuelle `new PactV4({...})`-Instanziierung durch den Aufruf `createProvider()`.
3. Füge der GET-Interaction eine Vorbedingung hinzu:

```typescript
await provider
  .addInteraction()
  .given('Sherlock is available')
  .uponReceiving('a request to get all customers (inkl. Sherlock)')
  .withRequest('GET', '/customers/')
  // ...
```

#### Provider-Seite (`CustomerServiceTest.java`)

Implementiere eine State-Handler-Methode, die für den Zustand `"Sherlock is available"` Sherlock Holmes in das Repository einfügt:

```java
@Inject
private CustomerRepository customerRepository;

@State("Sherlock is available")
public void insertSherlock() {
    customerRepository.persist(new Customer(new CustomerName("Sherlock Holmes")));
}
```

#### Provider-Seite: Zustand zurücksetzen (`TestCustomerRepository.java`)

Damit der Repository-Zustand nach jeder HTTP-Anfrage zurückgesetzt wird (Testfälle sollen sich nicht gegenseitig beeinflussen), erweitere `TestCustomerRepository` um eine Reset-Methode. Entferne gleichzeitig die `@RequestScoped`-Annotation, da der Repository-Scope durch die übergeordnete Klasse bestimmt wird:

```java
@Specializes
public class TestCustomerRepository extends CustomerRepository {

    public void reset(@Observes @Destroyed(RequestScoped.class) Object event) {
        super.initialize();
    }
}
```

Damit der Reset auch den internen Zähler für Kundennummern zurücksetzt, muss in `CustomerRepository.initialize()` der Zähler explizit auf 0 gesetzt werden:

```java
@PostConstruct
public void initialize() {
    CUSTOMER_NUMBERS.set(0);
    customers = new ConcurrentHashMap<>();
    // ...
}
```

### Ziel

Nach der Umsetzung:

- Nutzt `create-customer.spec.ts` die gemeinsame `createProvider()`-Hilfsfunktion und erzeugt einen Pact unter dem Consumer-Namen `customer-client`.
- Die GET-Interaction trägt den State `"Sherlock is available"`, der im erzeugten Pact-JSON dokumentiert ist.
- Der Provider-Test `CustomerServiceTest.java` kann alle Interaktionen erfolgreich verifizieren, weil er den State-Handler ausführt, bevor er die GET-Anfrage an den echten Service schickt.
- Der Datenbankzustand wird zwischen den Interaktionen automatisch zurückgesetzt, sodass die Tests unabhängig voneinander laufen.

