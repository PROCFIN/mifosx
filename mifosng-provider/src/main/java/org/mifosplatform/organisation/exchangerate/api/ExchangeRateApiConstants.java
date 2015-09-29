/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.exchangerate.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ExchangeRateApiConstants {

    public static final String EXCHANGE_RATE_RESOURCE_NAME = "exchangerate";

    // general
    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";

    // request parameters
    public static final String idParamName = "id";
    public static final String dateParamName = "date";
    public static final String typeParamName = "typeId";
    public static final String currencyParamName = "currency";
    public static final String amountParamName = "amount";

    public static final String allowedCurrenciesParamName = "allowedCurrencies";


    public static final Set<String> EXCHANGE_RATE_CREATE_OR_UPDATE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(
            typeParamName, currencyParamName, amountParamName, dateParamName, dateFormatParamName, localeParamName
    ));
    public static final Set<String> EXCHANGE_RATE_RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList(idParamName,
            typeParamName, currencyParamName, amountParamName, dateParamName, dateFormatParamName, localeParamName, allowedCurrenciesParamName
    ));

    public static final String TYPE_OPTION_CODE_NAME = "ExchangeRateType";
}
