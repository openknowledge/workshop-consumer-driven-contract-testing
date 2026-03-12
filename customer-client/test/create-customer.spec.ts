/*
 * Copyright 2026 open knowledge GmbH
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
import { test, expect } from '@playwright/test';
import { createProvider, setupApiProxy } from './pact-proxy';
import { PactV4 } from '@pact-foundation/pact';
import path from 'path';

test.describe('Neuer Kunde', () => {
  test('zeigt Fehler bei leerem Namen', async ({ page }) => {
    // Given
    await page.goto('/customers/new');

    // When
    await page.getByLabel('Name *').fill('Sherlock');
    await page.getByLabel('Name *').fill('');

    // Then
    await expect(page.locator('.field-error')).toContainText('Name ist erforderlich');
  });

  test('erstellt Kunden erfolgreich und navigiert zur Liste', async ({ page }) => {
    const provider = createProvider();

    provider
      .addInteraction()
      .uponReceiving('a request to create a customer')
      .withRequest('POST', '/customers/', (builder) => {
        builder
          .headers({ 'Content-Type': 'application/json' })
          .jsonBody({ name: 'Sherlock Holmes' });
      })
      .willRespondWith(201, (builder) => {
		builder.headers({
			"Location": "http://localhost:50376/customers/1"
		})
	  });

    await provider
      .addInteraction()
	  .given("Sherlock is available")
      .uponReceiving('a request to get all customers (inkl. Sherlock)')
      .withRequest('GET', '/customers/')
      .willRespondWith(200, (builder) => {
        builder.headers({ 'Content-Type': 'application/json' }).jsonBody([
          { number: '007', name: 'James Bond' },
          { number: '0815', name: 'Max Mustermann' },
          { number: '0816', name: 'Erika Mustermann' },
          { number: '1', name: 'Sherlock Holmes' },
        ]);
      })
      .executeTest(async (mockServer) => {
        await setupApiProxy(page, mockServer.url);
        await page.goto('/customers/new');

        // When
        await page.getByLabel('Name *').fill('Sherlock Holmes');
        await page.getByRole('button', { name: 'Kunde erstellen' }).click();

        await expect(page.getByRole('cell', { name: 'Sherlock Holmes' })).toBeVisible();
      });
  });
});
