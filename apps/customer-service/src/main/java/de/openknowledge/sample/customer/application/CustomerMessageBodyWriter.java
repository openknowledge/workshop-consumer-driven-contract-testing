/*
 * Copyright (C) open knowledge GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package de.openknowledge.sample.customer.application;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.util.AnnotationLiteral;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

import de.openknowledge.sample.customer.domain.Customer;

/**
 * Message body reader that transforms an entity of {@link CustomerResourceType}
 * to media type 'application/vnd.de.openknowledge.sample.customer.v2+json'.
 */
@Provider
@RequestScoped
@Produces(CustomMediaType.CUSTOMER_V2)
public class CustomerMessageBodyWriter implements MessageBodyWriter<Customer> {

    @Context
    private Providers providers;

    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
                               Annotation[] annotations, MediaType mediaType) {
        return type == Customer.class;
    }

    @Override
    public long getSize(Customer customerResourceType,
                        Class<?> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType) {
        // deprecated by JAX-RS 2.0
        return 0;
    }

    @Override
    public void writeTo(
            Customer customerResourceType,
            Class<?> type,
            Type genericType,
            Annotation[] annotations,
            MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream
    ) throws IOException, WebApplicationException {

        MessageBodyWriter<Customer> jsonWriter
                = providers.getMessageBodyWriter(
                Customer.class,
                Customer.class,
                addDefaultAnnotation(annotations),
                MediaType.APPLICATION_JSON_TYPE);

        jsonWriter.writeTo(
                customerResourceType,
                Customer.class,
                Customer.class,
                annotations,
                MediaType.APPLICATION_JSON_TYPE,
                httpHeaders,
                entityStream);
    }

    private Annotation[] addDefaultAnnotation(Annotation[] annotations) {
        Annotation[] newAnnotations = new Annotation[annotations.length + 1];
        System.arraycopy(annotations, 0, newAnnotations, 1, annotations.length);
        newAnnotations[0] = new AnnotationLiteral<Default>() {};
        return newAnnotations;
    }
}
