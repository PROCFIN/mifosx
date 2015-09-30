/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.forexexchange.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ForexExchangeApiConstants {

    public static final String FOREX_EXCHANGE_RESOURCE_NAME = "forexexchange";

    // general
    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";

    // request parameters
    public static final String idParamName = "id";
    public static final String exchangeRateIdParamName = "exchangeRateId";
    public static final String userIdParamName = "userId";
    public static final String currencyFromParamName = "currencyFrom";
    public static final String currencyToParamName = "currencyTo";
    public static final String clientIdParamName = "clientId";
    public static final String clientNameParamName = "clientName";
    public static final String transactionDateParamName = "transactionDate";
    public static final String amountGivenParamName = "amountGiven";
    public static final String amountTakenParamName = "amountTaken";

    public static final String amountParamName = "amount";

    public static final String allowedCurrenciesParamName = "allowedCurrencies";


    public static final Set<String> FOREX_EXCHANGE_CREATE_OR_UPDATE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(
            idParamName, exchangeRateIdParamName, userIdParamName, currencyFromParamName, currencyToParamName, clientIdParamName,
            clientNameParamName, transactionDateParamName, amountParamName,
            dateFormatParamName, localeParamName
    ));
    public static final Set<String> FOREX_EXCHANGE_RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList(idParamName,
            idParamName, exchangeRateIdParamName, userIdParamName, currencyFromParamName, currencyToParamName, clientIdParamName,
            clientNameParamName, transactionDateParamName, amountGivenParamName, amountTakenParamName,
            dateFormatParamName, localeParamName, allowedCurrenciesParamName
    ));

    public static final String TYPE_OPTION_CODE_NAME = "ExchangeRateType";
}
