# Workshop API Design

Herzlich willkommen zur Übung der Verwendung von Pact in der Build- und Deployment-Pipeline

## Setup starten

Starte das Setup mit docker compose

```
docker compose up --build
```

Es werden folgende Services gestartet:
- [Git Server](http://localhost:3030) (Nutzername: `openknowledge`, Password: `workshop`)
- [Jenkins](http://localhost:4040)
- [Pact Broker](http://localhost:5050)

In dem Git-Server werden während des Startups automatisch vier Projekte
für die bekannten vier Services angelegt.
Gleichzeitig starten im Jenkins automatisch Build-Prozesse, die die vier Services auf das Testsystem deployen.

Schaue im Jenkins nach, ob alle Pipelines durchgelaufen sind.

Wenn die vier Services deployt sind, kann die Swagger-UI des Customer Services auf dem Testsystem
über folgende URL erreicht werden: [Customer-Service](http://localhost:8281)

## Übung 1 - Verwendung des Pact-Brokers

Der Pact Broker ist bisher (außer eines Sample Contracts) leer. 

Wir wollen jetzt, dass die Services ihre Contracts via Pact austauschen.
Dazu muss in allen vier Services der Branch `pact-broker`
in den Branch `develop` gemergt werden.
Gehe auf den Git-Server und erstelle zunächst im `customer-service`
einen Pull-Request mit dem Base-Branch `develop`
und dem Vergleichs-Branch `pact-broker` und führe diese dann zusammen.
Die Jenkins-Pipeline sollte nun automatisch anfangen zu bauen
und den neuen Stand des Customer Services auf das Testsystem deployen.

Wenn das geschehen ist, kannst du im Pact-Broker sehen,
dass vom Customer Service zwei Contracts hochgeladen wurden,
einer für den Billing Service und einer für den Delivery Service.
Beide Contracts sind allerdings noch nicht verifiziert.

Wir mergen nun im Billing Service den Branch `pact-broker` in den Branch `develop`.
Dazu erstellen wir wieder, wie oben beschrieben, einen Pull-Request
und führen ihn zusammen.

Nachdem wir beobachtet haben, dass der Billing Service gebaut
und deployt wurde, können wir im Pact Broker sehen,
dass der Billing Service den Contract des Customer Services verifiziert hat.

Wir mergen nun analog den Delivery Service und beobachten auch hier,
dass er den Contract verifiziert.

Eine weitere Beobachtung wird sein,
dass der Delivery Service einen weiteren Contract hochgeladen hat,
nämlich für den Address Validation Service.

Zu guter Letzt mergen wir also den Branch `pact-broker`
auch im Address Validation Service in den Branch `develop`
und beobachten auch hier die Verifizierung.

## Übung 2 - Nur deployen, wenn der Branch verifiziert ist.

Es wäre wünschenswert, dass ein Service nur deployt wird,
wenn seine Contracts auch von den jeweiligen Providern verifiziert wurden.
Um das zu erreichen, müssen wir die Pipelines so umbauen,
dass die Consumer die Pipelines der Provider immer dann antriggern,
wenn sie einen neuen Contract hochgeladen haben.

Zunächst müssen wir allerdings die Provider-Pipelines so umbauen,
dass sie dann auch nur die Contracts verifizieren (und nicht selber deployen).
Dazu mergen wir zunächst im Address Validation Service und im Billing Service
jeweils den Branch `pipeline` in den Branch `develop`.

Ist das geschehen (und das jeweilige Build erfolgreich durchgelaufen),
können wir zunächst im Delivery Service den Branch `pipeline` in den Branch `develop` mergen.
Danach können wir beobachten, wie der Build des Delivery Services
den Build des Address Validation Services anstößt, dieser nur bis zur Phase Test baut
(und den Contract verifiziert) und dann der Build des Delivery Services fortgesetzt wird.

Anschließend können wir dasselbe im Customer Service beobachten,
wenn wir auch hier den Branch `pipeline` nach `develop` mergen.
Hier lässt sich beobachten, dass sowohl der Build des Billing Services
als auch der Build des Delivery Services angestoßen wird.

## Übung 3 - Verifizieren der Contracts von unterschiedlichen Stages

Um sicherzustellen, dass alle Versionen einer Stage zusammenpassen,
gibt es in Pact das Konzept der Branches und Environments.

Sobald ein Service in einer Version auf einer Stage deployt worden ist,
kann diese Version im Pact-Broker mit Tags für Branch und Environment (Stage) versehen werden.

Wir können das sehen, indem wir sowohl im Address Validation Service
als auch im Billing Service jeweils den Branch `pact-tags` in den Branch `develop` mergen.
Nach erfolgreichem Deployment sind die jeweiligen Versionen im Pact Broker jeweils mit
Branch `develop` und Environment `test` getaggt.

Wenn wir nun im Delivery Service den Branch `pact-tags` in `develop` mergen,
wird der Delivery Service im Pact Broker für den Contract mit dem Address Validation
Service mit dem Branch `develop` getaggt. In der Pipeline können wir beobachten, dass
versucht wird, die Pipeline des Address Validation Services zu starten. Wir merken jedoch,
dass sich da nichts tut. Das Problem ist, dass per Default in unserer Jenkins-Instanz
nur ein Build-Prozessor aktiv ist. Daher müssen wir in den Systemeinstellungen die Anzahl
der Build-Prozessoren auf 2 hochsetzen. Anschließend können wir beobachten, dass die Pipeline
des Address Validation Services parallel zu starten beginnt. Der Service verwendet beim
Verifizieren des Contracts die Version, die auch auf der Stage deployt ist, auf die der
Delivery Service deployen möchte (in diesem Fall die Test-Stage). Nachdem der Address Validation
Service den Contract verifiziert hat, darf der Delivery Services deployen und taggt seine Seite
des Contracts mit dem Environment `test`.

Dasselbe können wir erneut beim Customer Service beobachten,
wenn wir auch hier den Branch `pact-tags` in den Branch `develop` mergen.

## Übung 4 - Unabhängige Pipelines

Bisher ist es so, dass die Abhängigkeiten zwischen den Pipelines explizit
im `Jenkinsfile` eingetragen sind.
In der Praxis ist das nicht immer möglich.
Wenn Services von unterschiedlichen Teams umgesetzt werden und eventuell
sogar auf unterschiedlichen Build-Servern laufen,
werden andere Mechanismen benötigt, um die Verifikation von Contracts anzustoßen.

Der Pact Broker bietet hierzu die Möglichkeit,
zwei Arten von Webhooks zu registrieren.
Einerseits kann man einen Webhook registrieren,
der aufgerufen wird, wenn ein Contract hochgeladen wird.
Andererseits kann man einen Webhook registrieren,
der aufgerufen wird, wenn ein Contract verifiziert
und das Ergebnis hochgeladen wurde.
Ersteren werden wir verwenden, um die Verifikation anzustoßen.

Wir können die Webhooks registrieren, indem wir im Pact-Broker rechts oben
auf den `API Browser` gehen.
Dort suchen wir die Ressource `pb:webhooks` und klicken auf `Perform non-GET request`.
Das ist das gelb unterlegte `!`.

Insgesamt wollen wir fünf Webhooks anlegen:
zunächst einmal drei zum Antriggern der Verifikation
(nämlich für den Billing Service, den Delivery Service und den Address Validation Service).
Dazu führen wir drei POST-Requests mit den folgenden Bodys aus:

```
{
  "provider": {
    "name": "billing-service"
  },
  "events": [{
    "name": "contract_requiring_verification_published"
  }],
  "request": {
    "method": "GET",
    "url": "http://jenkins:8080/generic-webhook-trigger/invoke?token=billing-service&branch=${pactbroker.consumerVersionBranch}&verifyPacts=true",
    "headers": {
    },
    "body": {
      "event_type": "contract_requiring_verification_published",
      "client_payload": {
        "pact_url": "${pactbroker.pactUrl}",
        "sha": "${pactbroker.providerVersionNumber}",
        "branch": "${pactbroker.providerVersionBranch}",
        "message": "Verify changed pact for ${pactbroker.consumerName} version ${pactbroker.consumerVersionNumber} branch ${pactbroker.consumerVersionBranch} by ${pactbroker.providerVersionNumber} (${pactbroker.providerVersionDescriptions})"
      }
    }
  }
}
```

```
{
  "provider": {
    "name": "delivery-service"
  },
  "events": [{
    "name": "contract_requiring_verification_published"
  }],
  "request": {
    "method": "GET",
    "url": "http://jenkins:8080/generic-webhook-trigger/invoke?token=delivery-service&branch=${pactbroker.consumerVersionBranch}&verifyPacts=true",
    "headers": {
    },
    "body": {
      "event_type": "contract_requiring_verification_published",
      "client_payload": {
        "pact_url": "${pactbroker.pactUrl}",
        "sha": "${pactbroker.providerVersionNumber}",
        "branch": "${pactbroker.providerVersionBranch}",
        "message": "Verify changed pact for ${pactbroker.consumerName} version ${pactbroker.consumerVersionNumber} branch ${pactbroker.consumerVersionBranch} by ${pactbroker.providerVersionNumber} (${pactbroker.providerVersionDescriptions})"
      }
    }
  }
}
```

```
{
  "provider": {
    "name": "address-validation-service"
  },
  "events": [{
    "name": "contract_requiring_verification_published"
  }],
  "request": {
    "method": "GET",
    "url": "http://jenkins:8080/generic-webhook-trigger/invoke?token=address-validation-service&branch=${pactbroker.consumerVersionBranch}&verifyPacts=true",
    "headers": {
    },
    "body": {
      "event_type": "contract_requiring_verification_published",
      "client_payload": {
        "pact_url": "${pactbroker.pactUrl}",
        "sha": "${pactbroker.providerVersionNumber}",
        "branch": "${pactbroker.providerVersionBranch}",
        "message": "Verify changed pact for ${pactbroker.consumerName} version ${pactbroker.consumerVersionNumber} branch ${pactbroker.consumerVersionBranch} by ${pactbroker.providerVersionNumber} (${pactbroker.providerVersionDescriptions})"
      }
    }
  }
}
```

Außerdem legen wir mit weiteren POST-Requests noch zwei Webhooks an, die getriggert werden, wenn eine Verifikation erfolgreich war
und der Service auf die Stage deployen kann. Diese brauchen wir für den Customer Service und den Delivery Service.

```
{
  "consumer": {
    "name": "customer-service"
  },
  "events": [{
    "name": "provider_verification_published"
  }],
  "request": {
    "method": "GET",
    "url": "http://jenkins:8080/generic-webhook-trigger/invoke?token=customer-service&branch=${pactbroker.consumerVersionBranch}&deployOnly=true&deploymentVersion=${pactbroker.consumerVersionNumber}",
    "headers": {
    }
  }
}
```

```
{
  "consumer": {
    "name": "delivery-service"
  },
  "events": [{
    "name": "provider_verification_published"
  }],
  "request": {
    "method": "GET",
    "url": "http://jenkins:8080/generic-webhook-trigger/invoke?token=delivery-service&branch=${pactbroker.consumerVersionBranch}&deployOnly=true&deploymentVersion=${pactbroker.consumerVersionNumber}",
    "headers": {
    }
  }
}
```

Wenn wir alle Webhooks angelegt haben, können wir das Ganze testen,
indem wir jeweils die Branches `webhook` in den Branch `main` mergen.

Zunächst tun wir das für den Billing Service und den Address Validation Service und warten,
bis diese auf dem Produktivsystem deployt sind.

Danach mergen wir den Branch im Delivery Service. Nun können wir beobachten,
wie der Delivery Service gebaut und gepusht wird, aber noch nicht deployt wird,
weil sein hochgeladener Contract noch nicht für die Production Stage verifiziert ist.
Das Hochladen des Contracts hat aber bereits den Address Validation Service angestoßen,
der die Verifikation vornimmt. Sobald der Address Validation Service den Contract verifiziert hat,
wird über den Webhook wieder der Delivery Service angestoßen, der jetzt nur noch deployt.

Wenn wir im Customer Service auch den Branch `webhook` nach `main` mergen,
können wir hier dasselbe beobachten, nur dass der Customer Service die Verifikation
in Billing Service und Delivery Service anstößst und auch auf beide Verifikationen wartet.
