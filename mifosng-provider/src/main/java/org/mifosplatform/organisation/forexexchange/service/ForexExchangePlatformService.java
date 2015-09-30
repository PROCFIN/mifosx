/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.forexexchange.service;

import org.mifosplatform.organisation.exchangerate.data.ExchangeRateData;
import org.mifosplatform.organisation.forexexchange.data.ForexExchangeData;

import java.util.Collection;

public interface ForexExchangePlatformService {

    ForexExchangeData retrieveForexExchange(Long forexExchangeId);

    Collection<ForexExchangeData> retrieveAllForexExchanges(String sqlSearch);

    Collection<ForexExchangeData> retrieveCashierTransactions(Long staffId, String currencyCode);
}