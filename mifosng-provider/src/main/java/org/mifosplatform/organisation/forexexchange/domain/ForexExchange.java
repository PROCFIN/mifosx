/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.forexexchange.domain;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.organisation.exchangerate.domain.ExchangeRate;
import org.mifosplatform.organisation.forexexchange.api.ForexExchangeApiConstants;
import org.mifosplatform.organisation.office.domain.OrganisationCurrency;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Table(name = "m_forex_exchange")
public class ForexExchange extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "exchange_rate_id", nullable = false)
    private ExchangeRate exchangeRate;

    @ManyToOne
    @JoinColumn(name = "createdby_id", nullable = false)
    private AppUser user;

    @ManyToOne
    @JoinColumn(name = "currency_from_code", nullable = false, referencedColumnName = "code")
    private OrganisationCurrency currencyFrom;

    @ManyToOne
    @JoinColumn(name = "currency_to_code", nullable = false, referencedColumnName = "code")
    private OrganisationCurrency currencyTo;

    @Column(name = "client_id", length = 50)
    private String clientId;

    @Column(name = "client_name", length = 50)
    private String clientName;

    @Column(name = "transaction_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date transactionDate;

    @Column(name = "created_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(name = "amount_given", scale = 6, precision = 19)
    private BigDecimal amountGiven;

    @Column(name = "amount_taken", scale = 6, precision = 19)
    private BigDecimal amountTaken;

    protected ForexExchange() {
    }

    private ForexExchange(ExchangeRate exchangeRate,
                          AppUser user,
                          OrganisationCurrency currencyFrom,
                          OrganisationCurrency currencyTo,
                          String clientId,
                          String clientName,
                          Date transactionDate,
                          BigDecimal amountGiven,
                          BigDecimal amountTaken) {
        this.exchangeRate = exchangeRate;
        this.user = user;
        this.currencyFrom = currencyFrom;
        this.currencyTo = currencyTo;
        this.clientId = clientId;
        this.clientName = clientName;
        this.transactionDate = transactionDate;
        this.amountGiven = amountGiven;
        this.amountTaken = amountTaken;
        this.createdDate = new Date();
    }

    private ForexExchange(ExchangeRate exchangeRate,
                          AppUser user,
                          OrganisationCurrency currencyFrom,
                          OrganisationCurrency currencyTo,
                          String clientId,
                          String clientName,
                          Date transactionDate) {
        this(exchangeRate, user, currencyFrom, currencyTo, clientId, clientName, transactionDate, null, null);
    }

    public static ForexExchange fromJson(final JsonCommand command,
                                         ExchangeRate exchangeRate,
                                         AppUser appUser,
                                         OrganisationCurrency currencyFrom,
                                         OrganisationCurrency currencyTo) {
        final String clientId = command.stringValueOfParameterNamed(ForexExchangeApiConstants.clientIdParamName);

        final String clientName = command.stringValueOfParameterNamed(ForexExchangeApiConstants.clientNameParamName);
        Date transactionDate = null;
        if (command.hasParameter(ForexExchangeApiConstants.transactionDateParamName)) {
            transactionDate = command.localDateValueOfParameterNamed(ForexExchangeApiConstants.transactionDateParamName).toDate();
        }
        return new ForexExchange(exchangeRate, appUser, currencyFrom, currencyTo, clientId, clientName, transactionDate);
    }

    public Map<String, Object> update(final JsonCommand command) {
        final Map<String, Object> actualChanges = new LinkedHashMap<>(5);

        if (command.isChangeInLongParameterNamed(ForexExchangeApiConstants.exchangeRateIdParamName, this.exchangeRate.getId())) {
            final Long newValue = command.longValueOfParameterNamed(ForexExchangeApiConstants.exchangeRateIdParamName);
            actualChanges.put(ForexExchangeApiConstants.exchangeRateIdParamName, newValue);
        }

        if (command.isChangeInStringParameterNamed(ForexExchangeApiConstants.currencyFromParamName, this.currencyFrom.getCode())) {
            final String newValue = command.stringValueOfParameterNamed(ForexExchangeApiConstants.currencyFromParamName);
            actualChanges.put(ForexExchangeApiConstants.currencyFromParamName, newValue);
        }

        if (command.isChangeInStringParameterNamed(ForexExchangeApiConstants.currencyToParamName, this.currencyTo.getCode())) {
            final String newValue = command.stringValueOfParameterNamed(ForexExchangeApiConstants.currencyToParamName);
            actualChanges.put(ForexExchangeApiConstants.currencyToParamName, newValue);
        }

        if (command.isChangeInStringParameterNamed(ForexExchangeApiConstants.clientIdParamName, this.clientId)) {
            final String newValue = command.stringValueOfParameterNamed(ForexExchangeApiConstants.clientIdParamName);
            actualChanges.put(ForexExchangeApiConstants.clientIdParamName, newValue);
            this.clientId = newValue;
        }

        if (command.isChangeInStringParameterNamed(ForexExchangeApiConstants.clientNameParamName, this.clientName)) {
            final String newValue = command.stringValueOfParameterNamed(ForexExchangeApiConstants.clientNameParamName);
            actualChanges.put(ForexExchangeApiConstants.clientNameParamName, newValue);
            this.clientName = newValue;
        }

        if (command.isChangeInLocalDateParameterNamed(ForexExchangeApiConstants.transactionDateParamName, LocalDate.fromDateFields(this.transactionDate))) {
            final LocalDate newValue = command.localDateValueOfParameterNamed(ForexExchangeApiConstants.transactionDateParamName);
            actualChanges.put(ForexExchangeApiConstants.transactionDateParamName, newValue);
            this.transactionDate = newValue.toDate();
        }

        if (command.isChangeInBigDecimalParameterNamed(ForexExchangeApiConstants.amountGivenParamName, this.amountGiven)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(ForexExchangeApiConstants.amountGivenParamName);
            actualChanges.put(ForexExchangeApiConstants.amountGivenParamName, newValue);
            this.amountGiven = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(ForexExchangeApiConstants.amountTakenParamName, this.amountTaken)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(ForexExchangeApiConstants.amountTakenParamName);
            actualChanges.put(ForexExchangeApiConstants.amountTakenParamName, newValue);
            this.amountTaken = newValue;
        }

        return actualChanges;
    }

    public boolean identifiedBy(final ForexExchange forexExchange) {
        return getId().equals(forexExchange.getId());
    }

    public OrganisationCurrency getCurrencyFrom() {
        return currencyFrom;
    }

    public void setCurrencyFrom(OrganisationCurrency currencyFrom) {
        this.currencyFrom = currencyFrom;
    }

    public OrganisationCurrency getCurrencyTo() {
        return currencyTo;
    }

    public void setCurrencyTo(OrganisationCurrency currencyTo) {
        this.currencyTo = currencyTo;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public ExchangeRate getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(ExchangeRate exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public BigDecimal getAmountGiven() {
        return amountGiven;
    }

    public void setAmountGiven(BigDecimal amountGiven) {
        this.amountGiven = amountGiven;
    }

    public BigDecimal getAmountTaken() {
        return amountTaken;
    }

    public void setAmountTaken(BigDecimal amountTaken) {
        this.amountTaken = amountTaken;
    }
}