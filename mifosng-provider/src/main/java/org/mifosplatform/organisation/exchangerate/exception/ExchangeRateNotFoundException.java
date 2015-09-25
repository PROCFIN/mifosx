/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.exchangerate.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

import java.util.Date;

/**
 * A {@link RuntimeException} thrown when staff resources are not found.
 */
public class ExchangeRateNotFoundException extends AbstractPlatformResourceNotFoundException {

    public ExchangeRateNotFoundException(final Long id) {
        super("error.msg.exchange.rate.id.invalid", "Exchange rate with identifier " + id + " does not exist", id);
    }
    public ExchangeRateNotFoundException(final String currency, final int rateType) {
        super("error.msg.exchange.rate.currency.invalid", "Exchange rate with currency '" + currency + "' with rate type identified with '" + rateType + "' does not exist", currency, rateType);
    }
}