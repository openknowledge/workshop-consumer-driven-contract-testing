/*
 * Copyright 2019 open knowledge GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.openknowledge.sample.address;

import static java.util.Optional.ofNullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.sse.InboundSseEvent;
import jakarta.ws.rs.sse.SseEventSource;

import org.apache.meecrowave.Meecrowave;
import org.apache.meecrowave.junit5.MeecrowaveConfig;
import org.apache.meecrowave.testing.ConfigurationInject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import au.com.dius.pact.provider.PactVerifyProvider;
import au.com.dius.pact.provider.junit5.MessageTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;

@Provider("billing-service")
@PactFolder("src/test/pacts/messaging")
@MeecrowaveConfig
public class BillingAddressServiceMessagingTest {

    @ConfigurationInject
    private Meecrowave.Builder config;

    @BeforeEach
    public void setUp(PactVerificationContext verificationContext) {
        ofNullable(verificationContext)
            .ifPresent(context -> context.setTarget(new MessageTestTarget()));
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        ofNullable(context).ifPresent(PactVerificationContext::verifyInteraction);
    }

    @PactVerifyProvider("Update for 0815")
    public String updateBillingAddress() throws InterruptedException, ExecutionException, TimeoutException {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:" + config.getActivePort() + "/billing-addresses/0815");
        CompletableFuture<InboundSseEvent> futureEvent = new CompletableFuture<>();
        SseEventSource source = SseEventSource.target(target).build();
        source.register(futureEvent::complete);
        source.open();

        // TODO POST file 0816.json to trigger event

        return futureEvent.get(5, TimeUnit.SECONDS).readData();
    }

    @State("Three customers")
    public void toDefaultState() {
        // Nothing to do here
    }
}
