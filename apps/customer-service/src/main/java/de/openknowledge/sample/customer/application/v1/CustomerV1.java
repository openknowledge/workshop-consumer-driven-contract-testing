/*
 * Copyright 2019 open knowledge GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.openknowledge.sample.customer.application.v1;

import static java.util.Optional.ofNullable;

import javax.json.bind.annotation.JsonbProperty;

import de.openknowledge.sample.address.domain.Address;
import de.openknowledge.sample.customer.domain.Customer;
import de.openknowledge.sample.customer.domain.CustomerName;
import de.openknowledge.sample.customer.domain.CustomerNumber;
import de.openknowledge.sample.customer.domain.Name;

public class CustomerV1 {

    CustomerNumber number;
    private Name fullName;
    private Address billingAddress;
    private Address deliveryAddress;

    public CustomerV1() {
        // for frameworks
    }

    public CustomerV1(Customer customer) {
        this.number = customer.getNumber();
        this.fullName = customer.getFullName();
        this.billingAddress = customer.getBillingAddress();
        this.deliveryAddress = customer.getDeliveryAddress();
    }

    public Name getFullName() {
        return fullName;
    }

    public void setFullName(Name name) {
    	this.fullName = name;
    }

    public CustomerName getName() {
    	return new CustomerName(fullName.getFirstName() + " " + fullName.getLastName());
    }

    public void setName(CustomerName name) {
    	int lastIndex = name.toString().lastIndexOf(' ');
    	if (lastIndex < 0) {
    		this.fullName = new Name(name.toString(), name.toString());
    	} else {
    		this.fullName = new Name(name.toString().substring(0, lastIndex), name.toString().substring(lastIndex + 1));
    	}
    }

    public CustomerNumber getNumber() {
        return number;
    }

    @JsonbProperty(nillable = false)
    public Address getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }

    @JsonbProperty(nillable = false)
    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    CustomerV1 clearAddresses() {
        deliveryAddress = null;
        billingAddress = null;
        return this;
    }

    @Override
    public int hashCode() {
        return fullName.hashCode() ^ number.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof CustomerV1)) {
            return false;
        }

        CustomerV1 customer = (CustomerV1) object;

        return fullName.equals(customer.getFullName()) && number.equals(customer.getNumber());
    }

    @Override
    public String toString() {
        return fullName + "(" + number + ")";
    }

    public Customer toV2() {
        Customer customer = ofNullable(number)
            .map(customerNumber -> new Customer(number, fullName))
            .orElseGet(() -> new Customer(fullName));
        customer.setBillingAddress(billingAddress);
        customer.setDeliveryAddress(deliveryAddress);
        return customer;
    }
}
