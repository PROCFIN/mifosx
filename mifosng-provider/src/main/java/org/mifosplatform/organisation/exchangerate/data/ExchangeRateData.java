/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.exchangerate.data;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.mifosplatform.organisation.monetary.data.CurrencyData;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Immutable data object representing staff data.
 */
public class ExchangeRateData {

    private final Long id;
    private final LocalDate date;
    private final String type;
    private final String currency;
    private final BigDecimal amount;

    @SuppressWarnings("unused")
    private final Collection<CurrencyData> allowedCurrencies;

    public ExchangeRateData(Long id, LocalDate date, String type, String currency, BigDecimal amount) {
        this(id, date, type, currency, amount, null);
    }

    public ExchangeRateData(Long id, LocalDate date, String type, String currency, BigDecimal amount, Collection<CurrencyData> allowedCurrencies) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.currency = currency;
        this.amount = amount;
        this.allowedCurrencies = allowedCurrencies;
    }

    public static ExchangeRateData lookup(final Long id) {
        return new ExchangeRateData(id, null, null, null, null);
    }

    public static ExchangeRateData templateData(final ExchangeRateData exchangeRateData, final Collection<CurrencyData> allowedCurrencies) {
        return new ExchangeRateData(exchangeRateData.id, exchangeRateData.date, exchangeRateData.type, exchangeRateData.currency, exchangeRateData.amount, allowedCurrencies);
    }

    public static ExchangeRateData instance(Long id, LocalDate date, String type, String currency, BigDecimal amount) {
        return new ExchangeRateData(id, date, type, currency, amount);
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}