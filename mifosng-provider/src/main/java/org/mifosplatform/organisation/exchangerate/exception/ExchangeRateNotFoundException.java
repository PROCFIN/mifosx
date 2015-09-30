/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.exchangerate.exception;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;
import org.mifosplatform.organisation.exchangerate.domain.ExchangeRateType;

import java.util.Date;

/**
 * A {@link RuntimeException} thrown when staff resources are not found.
 */
public class ExchangeRateNotFoundException extends AbstractPlatformResourceNotFoundException {

    public ExchangeRateNotFoundException(final Long id) {
        super("error.msg.exchange.rate.id.invalid", "Exchange rate with identifier " + id + " does not exist", id);
    }
    public ExchangeRateNotFoundException(final String currency, final ExchangeRateType rateType) {
        super("error.msg.exchange.rate.currency.invalid", "Exchange rate for currency '" + currency + "' with '" + rateType.getCode() + "' rate type does not exist", currency, rateType.getCode());
    }
    public ExchangeRateNotFoundException(final String currency, final ExchangeRateType rateType, LocalDate localDate) {
        super("error.msg.exchange.rate.currency.invalid", "Exchange rate for currency '" + currency + "' with '" + rateType.getCode() + "' rate type before '" + localDate.toString() + "' does not exist", currency, rateType.getCode(), localDate);
    }
}