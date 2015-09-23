/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.exchangerate.domain;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Table(name = "m_exchange_rate")
public class ExchangeRate extends AbstractPersistable<Long> {

    @Column(name = "date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "currency", length = 3)
    private String currency;

    @Column(name = "amount", scale = 6, precision = 19)
    private BigDecimal amount;


    public static ExchangeRate fromJson(final JsonCommand command) {

        final String dateParamName = "date";
        Date date = null;
        if (command.hasParameter(dateParamName)) {
            date = command.localDateValueOfParameterNamed(dateParamName).toDate();
        }

        final String typeParamName = "type";
        final String type = command.stringValueOfParameterNamed(typeParamName);

        final String currencyParamName = "currency";
        final String currency = command.stringValueOfParameterNamed(currencyParamName);

        final String amountParamName = "amount";
        final BigDecimal amount = command.bigDecimalValueOfParameterNamed(amountParamName);

        return new ExchangeRate(date, type, currency, amount);
    }

    protected ExchangeRate() {
        //
    }

    private ExchangeRate(Date date, String type, String currency, BigDecimal amount) {

        this.type = type;
        this.currency = currency;
        this.amount = amount;
        this.date = date;
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(4);

        final String dateParamName = "date";
        if (command.isChangeInLocalDateParameterNamed(dateParamName, LocalDate.fromDateFields(this.date))) {
            final LocalDate newValue = command.localDateValueOfParameterNamed(dateParamName);
            actualChanges.put(dateParamName, newValue);
            this.date = newValue.toDate();
        }

        final String typeParamName = "type";
        if (command.isChangeInStringParameterNamed(typeParamName, this.type)) {
            final String newValue = command.stringValueOfParameterNamed(typeParamName);
            actualChanges.put(typeParamName, newValue);
            this.type = newValue;
        }

        final String currencyParamName = "currency";
        if (command.isChangeInStringParameterNamed(currencyParamName, this.currency)) {
            final String newValue = command.stringValueOfParameterNamed(currencyParamName);
            actualChanges.put(currencyParamName, newValue);
            this.currency = newValue;
        }

        final String amountParamName = "amount";
        if (command.isChangeInBigDecimalParameterNamed(amountParamName, this.amount)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(amountParamName);
            actualChanges.put(amountParamName, newValue);
            this.amount = newValue;
        }
        return actualChanges;
    }

    public boolean identifiedBy(final ExchangeRate exchangeRate) {
        return getId().equals(exchangeRate.getId());
    }
}