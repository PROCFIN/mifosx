/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.exchangerate.domain;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.codes.domain.CodeValue;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.organisation.exchangerate.api.ExchangeRateApiConstants;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_cv_id")
    private CodeValue rateType;

    @Column(name = "currency", length = 3)
    private String currency;

    @Column(name = "amount", scale = 6, precision = 19)
    private BigDecimal amount;


    public static ExchangeRate fromJson(final JsonCommand command, CodeValue rateType) {

        Date date = null;
        if (command.hasParameter(ExchangeRateApiConstants.dateParamName)) {
            date = command.localDateValueOfParameterNamed(ExchangeRateApiConstants.dateParamName).toDate();
        }

//        final Long typeId = command.longValueOfParameterNamed(ExchangeRateApiConstants.typeParamName);

        final String currency = command.stringValueOfParameterNamed(ExchangeRateApiConstants.currencyParamName);

        final BigDecimal amount = command.bigDecimalValueOfParameterNamed(ExchangeRateApiConstants.amountParamName);

        return new ExchangeRate(date, rateType, currency, amount);
    }

    protected ExchangeRate() {
        //
    }

    private ExchangeRate(Date date, CodeValue rateType, String currency, BigDecimal amount) {

        this.rateType = rateType;
        this.currency = currency;
        this.amount = amount;
        this.date = date;
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(4);

        if (command.isChangeInLocalDateParameterNamed(ExchangeRateApiConstants.dateParamName, LocalDate.fromDateFields(this.date))) {
            final LocalDate newValue = command.localDateValueOfParameterNamed(ExchangeRateApiConstants.dateParamName);
            actualChanges.put(ExchangeRateApiConstants.dateParamName, newValue);
            this.date = newValue.toDate();
        }

        if (command.isChangeInLongParameterNamed(ExchangeRateApiConstants.typeParamName, this.rateType == null ? 0L : this.rateType.getId())) {
            final Long newValue = command.longValueOfParameterNamed(ExchangeRateApiConstants.typeParamName);
            actualChanges.put(ExchangeRateApiConstants.typeParamName, newValue);

//            this.rateType = newValue;
        }

        if (command.isChangeInStringParameterNamed(ExchangeRateApiConstants.currencyParamName, this.currency)) {
            final String newValue = command.stringValueOfParameterNamed(ExchangeRateApiConstants.currencyParamName);
            actualChanges.put(ExchangeRateApiConstants.currencyParamName, newValue);
            this.currency = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(ExchangeRateApiConstants.amountParamName, this.amount)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(ExchangeRateApiConstants.amountParamName);
            actualChanges.put(ExchangeRateApiConstants.amountParamName, newValue);
            this.amount = newValue;
        }
        return actualChanges;
    }

    public boolean identifiedBy(final ExchangeRate exchangeRate) {
        return getId().equals(exchangeRate.getId());
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setRateType(CodeValue rateType) {
        this.rateType = rateType;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}