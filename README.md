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

