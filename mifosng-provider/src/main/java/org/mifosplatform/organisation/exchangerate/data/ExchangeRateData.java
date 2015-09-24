/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.exchangerate.data;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.organisation.monetary.data.CurrencyData;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Immutable data object representing staff data.
 */
public class ExchangeRateData {

    private final Long id;
    private final LocalDate date;
    private final CodeValueData rateType;
    private final String currency;
    private final BigDecimal amount;

    @SuppressWarnings("unused")
    private final Collection<CurrencyData> allowedCurrencies;
    private final Collection<CodeValueData> allowedExchangeRateTypeOptions;

    public ExchangeRateData(Long id, LocalDate date, CodeValueData rateType, String currency, BigDecimal amount) {
        this(id, date, rateType, currency, amount, null, null);
    }

    public ExchangeRateData(ExchangeRateData exchangeRateData, Collection<CurrencyData> allowedCurrencies, Collection<CodeValueData> allowedExchangeRateTypeOptions) {
        this(exchangeRateData.id, exchangeRateData.date, exchangeRateData.rateType, exchangeRateData.currency, exchangeRateData.amount, allowedCurrencies, allowedExchangeRateTypeOptions);
    }

    public ExchangeRateData(Long id, LocalDate date, CodeValueData rateType, String currency, BigDecimal amount, Collection<CurrencyData> allowedCurrencies, Collection<CodeValueData> allowedExchangeRateTypeOptions) {
        this.id = id;
        this.date = date;
        this.rateType = rateType;
        this.currency = currency;
        this.amount = amount;
        this.allowedCurrencies = allowedCurrencies;
        this.allowedExchangeRateTypeOptions = allowedExchangeRateTypeOptions;
    }

    public static ExchangeRateData templateData(final ExchangeRateData exchangeRateData, final Collection<CurrencyData> allowedCurrencies, Collection<CodeValueData> allowedExchangeRateTypeOptions) {
        return new ExchangeRateData(exchangeRateData.id, exchangeRateData.date, exchangeRateData.rateType, exchangeRateData.currency, exchangeRateData.amount, allowedCurrencies, allowedExchangeRateTypeOptions);
    }

    public static ExchangeRateData sensibleDefaultsForNewExchangeRateCreation() {
        return new ExchangeRateData(null, null, null, null, null);
    }

    public static ExchangeRateData instance(Long id, LocalDate date, CodeValueData rateType, String currency, BigDecimal amount) {
        return new ExchangeRateData(id, date, rateType, currency, amount);
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public CodeValueData getType() {
        return rateType;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}