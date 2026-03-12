# Workshop API Design

Herzlich willkommen zum Workshop API Design.

## Übung: Provider States verwenden

### Ausgangssituation

Führt man die Playwright-Tests aus, entstehen zwei Contracts.
Lässt man danach den `CustomerServiceTest.java` laufen, schlägt einer der Tests fehl.

### Aufgabe

Analysiere das Problem und überlege dir Lösungsmöglichkeiten.
Eine Möglichkeit wäre es, die erste auskommentierte Zeile der Methode `setUp`
in `CustomerServiceTest.java` einzukommentieren.
Warum führt das nicht zum gewünschten Erfolg.

Behebe das Problem so, dass sowohl die Consumer-Tests
als auch die anschließende Provider-Verifikation erfolgreich ist.

### Ziel

Nach der Implementierung verifiziert `CustomerServiceTest.java`
den entstandenen Contract erfolgreich.

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

