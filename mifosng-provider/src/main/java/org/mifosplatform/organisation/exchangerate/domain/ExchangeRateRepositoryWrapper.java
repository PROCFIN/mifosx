/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.exchangerate.domain;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.codes.domain.CodeValue;
import org.mifosplatform.organisation.exchangerate.exception.ExchangeRateNotFoundException;
import org.mifosplatform.organisation.staff.exception.StaffNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Wrapper for {@link ExchangeRateRepository} that adds NULL checking and Error
 * handling capabilities
 * </p>
 */
@Service
public class ExchangeRateRepositoryWrapper {

    private final ExchangeRateRepository repository;

    @Autowired
    public ExchangeRateRepositoryWrapper(final ExchangeRateRepository repository) {
        this.repository = repository;
    }

    public ExchangeRate findOneWithNotFoundDetection(final Long id) {
        final ExchangeRate exchangeRate = this.repository.findOne(id);
        if (exchangeRate == null) { throw new ExchangeRateNotFoundException(id); }
        return exchangeRate;
    }

    public ExchangeRate findOneByCurrencyAndTypeWithNotFoundDetection(final String currencyCode, int rateTypeId) {
        LocalDate localDate = new LocalDate().plusDays(1);
        final ExchangeRate exchangeRate = this.repository.findByCurrencyAndTypeAndDateBefore(currencyCode, rateTypeId, localDate.toDate());
        if (exchangeRate == null) { throw new ExchangeRateNotFoundException(currencyCode, ExchangeRateType.fromInt(rateTypeId)); }
        return exchangeRate;
    }

    public void save(final ExchangeRate exchangeRate){
        this.repository.save(exchangeRate);
    }
}