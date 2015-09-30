/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.forexexchange.data;

import org.joda.time.LocalDate;
import org.mifosplatform.organisation.exchangerate.data.ExchangeRateData;
import org.mifosplatform.organisation.monetary.data.CurrencyData;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Immutable data object representing forex exchange data.
 */
public class ForexExchangeData {

    private final Long id;
    private final ExchangeRateData exchangeRate;
    private final Long createdById;
    private final String createdByUserName;
    private final CurrencyData currencyFrom;
    private final CurrencyData currencyTo;
    private final String clientId;
    private final String clientName;
    private final LocalDate transactionDate;
    private final LocalDate createdDate;
    private final BigDecimal amountGiven;
    private final BigDecimal amountTaken;

    @SuppressWarnings("unused")
    private final Collection<CurrencyData> allowedCurrencies;

    public ForexExchangeData(Long id,
                             ExchangeRateData exchangeRate,
                             Long createdById,
                             String createdByUserName,
                             CurrencyData currencyFrom,
                             CurrencyData currencyTo,
                             String clientId,
                             String clientName,
                             LocalDate transactionDate,
                             LocalDate createdDate,
                             BigDecimal amountGiven,
                             BigDecimal amountTaken,
                             Collection<CurrencyData> allowedCurrencies) {
        this.id = id;
        this.exchangeRate = exchangeRate;
        this.createdById = createdById;
        this.createdByUserName = createdByUserName;
        this.currencyFrom = currencyFrom;
        this.currencyTo = currencyTo;
        this.clientId = clientId;
        this.clientName = clientName;
        this.transactionDate = transactionDate;
        this.createdDate = createdDate;
        this.amountGiven = amountGiven;
        this.amountTaken = amountTaken;
        this.allowedCurrencies = allowedCurrencies;
    }

    public ForexExchangeData(Long id,
                             ExchangeRateData exchangeRate,
                             Long createdById,
                             String createdByUserName,
                             CurrencyData currencyFrom,
                             CurrencyData currencyTo,
                             String clientId,
                             String clientName,
                             LocalDate transactionDate,
                             LocalDate createdDate,
                             BigDecimal amountGiven,
                             BigDecimal amountTaken) {
        this(id, exchangeRate, createdById, createdByUserName, currencyFrom, currencyTo, clientId, clientName, transactionDate, createdDate, amountGiven, amountTaken, null);
    }

    public ForexExchangeData(ForexExchangeData forexExchangeData, Collection<CurrencyData> allowedCurrencies) {
        this(forexExchangeData.id,
                forexExchangeData.exchangeRate,
                forexExchangeData.createdById,
                forexExchangeData.createdByUserName,
                forexExchangeData.currencyFrom,
                forexExchangeData.currencyTo,
                forexExchangeData.clientId,
                forexExchangeData.clientName,
                forexExchangeData.transactionDate,
                forexExchangeData.createdDate,
                forexExchangeData.amountGiven,
                forexExchangeData.amountTaken,
                allowedCurrencies);
    }

    public static ForexExchangeData templateData(final ForexExchangeData forexExchangeData,
                                                 final Collection<CurrencyData> allowedCurrencies) {
        return new ForexExchangeData(forexExchangeData, allowedCurrencies);
    }

    public static ForexExchangeData sensibleDefaultsForNewForexExchangeCreation() {
        return new ForexExchangeData(null, null, null, null, null, null, null, null, null, null, null, null);
    }

    public static ForexExchangeData instance(Long id,
                                             ExchangeRateData exchangeRate,
                                             Long createdById,
                                             String createdByUserName,
                                             CurrencyData currencyFrom,
                                             CurrencyData currencyTo,
                                             String clientId,
                                             String clientName,
                                             LocalDate transactionDate,
                                             LocalDate createdDate,
                                             BigDecimal amountGiven,
                                             BigDecimal amountTaken) {
        return new ForexExchangeData(id, exchangeRate, createdById, createdByUserName, currencyFrom, currencyTo, clientId, clientName, transactionDate, createdDate, amountGiven, amountTaken);
    }
}