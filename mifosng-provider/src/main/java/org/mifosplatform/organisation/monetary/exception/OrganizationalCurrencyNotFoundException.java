/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.monetary.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * A {@link RuntimeException} thrown when urrency is not supported by an
 * Organization.
 */
public class OrganizationalCurrencyNotFoundException extends AbstractPlatformResourceNotFoundException {

    public OrganizationalCurrencyNotFoundException(final String currencyCode) {
        super("error.msg.currency.currencyCode.invalid.or.not.supported", "Currency with identifier " + currencyCode
                + " does not exist or is not supported by the Organization", currencyCode);
    }
    public OrganizationalCurrencyNotFoundException() {
        super("error.msg.currency.home.is.not.set", "Home currency does not exist or is not supported by the Organization");
    }
}