/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.forexexchange.domain;

import org.mifosplatform.organisation.forexexchange.exception.ForexExchangeNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Wrapper for {@link org.mifosplatform.organisation.forexexchange.domain.ForexExchangeRepository} that adds NULL checking and Error
 * handling capabilities
 * </p>
 */
@Service
public class ForexExchangeRepositoryWrapper {

    private final ForexExchangeRepository repository;

    @Autowired
    public ForexExchangeRepositoryWrapper(final ForexExchangeRepository repository) {
        this.repository = repository;
    }

    public ForexExchange findOneWithNotFoundDetection(final Long id) {
        final ForexExchange forexExchange = this.repository.findOne(id);
        if (forexExchange == null) {
            throw new ForexExchangeNotFoundException(id);
        }
        return forexExchange;
    }

    public void save(final ForexExchange exchangeRate) {
        this.repository.save(exchangeRate);
    }
}