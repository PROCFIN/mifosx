/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.exchangerate.service;

import org.mifosplatform.organisation.exchangerate.data.ExchangeRateData;

import java.util.Collection;

public interface ExchangeRatePlatformService {

    ExchangeRateData retrieveExchangeRate(Long exchangeRateId);

//    Collection<ExchangeRateData> retrieveAllExchangeRatesForDropdown();

    Collection<ExchangeRateData> retrieveAllExchangeRates(String sqlSearch);
}