/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.forexexchange.serialization;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.organisation.exchangerate.api.ExchangeRateApiConstants;
import org.mifosplatform.organisation.forexexchange.api.ForexExchangeApiConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public final class ForexExchangeCommandFromApiJsonDeserializer {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public ForexExchangeCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {
        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {
        }.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, ForexExchangeApiConstants.FOREX_EXCHANGE_CREATE_OR_UPDATE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource(ForexExchangeApiConstants.FOREX_EXCHANGE_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final String currencyFrom = this.fromApiJsonHelper.extractStringNamed(ForexExchangeApiConstants.currencyFromParamName, element);
        baseDataValidator.reset().parameter(ForexExchangeApiConstants.currencyFromParamName).value(currencyFrom).notBlank().notExceedingLengthOf(3);

        final String currencyTo = this.fromApiJsonHelper.extractStringNamed(ForexExchangeApiConstants.currencyToParamName, element);
        baseDataValidator.reset().parameter(ForexExchangeApiConstants.currencyToParamName).value(currencyTo).notBlank().notExceedingLengthOf(3);

        final String clientId = this.fromApiJsonHelper.extractStringNamed(ForexExchangeApiConstants.clientIdParamName, element);
        baseDataValidator.reset().parameter(ForexExchangeApiConstants.clientIdParamName).value(clientId).ignoreIfNull().notExceedingLengthOf(50);

        final String clientName = this.fromApiJsonHelper.extractStringNamed(ForexExchangeApiConstants.clientNameParamName, element);
        baseDataValidator.reset().parameter(ForexExchangeApiConstants.clientNameParamName).value(clientName).ignoreIfNull().notExceedingLengthOf(50);

        final LocalDate transactionDate = this.fromApiJsonHelper.extractLocalDateNamed(ForexExchangeApiConstants.transactionDateParamName, element);
        baseDataValidator.reset().parameter(ForexExchangeApiConstants.transactionDateParamName).value(transactionDate).notNull();

        final BigDecimal amount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(ForexExchangeApiConstants.amountParamName, element);
        baseDataValidator.reset().parameter(ForexExchangeApiConstants.amountParamName).value(amount).notNull().positiveAmount();

        if (this.fromApiJsonHelper.parameterExists(ForexExchangeApiConstants.localeParamName, element)) {
            final String locale = this.fromApiJsonHelper.extractStringNamed(ForexExchangeApiConstants.localeParamName, element);
            baseDataValidator.reset().parameter(ForexExchangeApiConstants.localeParamName).value(locale).notBlank();
        }

        if (this.fromApiJsonHelper.parameterExists(ForexExchangeApiConstants.dateFormatParamName, element)) {
            final String dateFormat = this.fromApiJsonHelper.extractStringNamed(ForexExchangeApiConstants.dateFormatParamName, element);
            baseDataValidator.reset().parameter(ForexExchangeApiConstants.dateFormatParamName).value(dateFormat).notBlank();
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateForUpdate(final String json) {
        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {
        }.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, ExchangeRateApiConstants.EXCHANGE_RATE_CREATE_OR_UPDATE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource(ExchangeRateApiConstants.EXCHANGE_RATE_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        if (this.fromApiJsonHelper.parameterExists(ForexExchangeApiConstants.exchangeRateIdParamName, element)) {
            final Long exchangeRateId = this.fromApiJsonHelper.extractLongNamed(ForexExchangeApiConstants.exchangeRateIdParamName, element);
            baseDataValidator.reset().parameter(ForexExchangeApiConstants.exchangeRateIdParamName).value(exchangeRateId).notNull();
        }

        if (this.fromApiJsonHelper.parameterExists(ForexExchangeApiConstants.currencyFromParamName, element)) {
            final String currencyFrom = this.fromApiJsonHelper.extractStringNamed(ForexExchangeApiConstants.currencyFromParamName, element);
            baseDataValidator.reset().parameter(ForexExchangeApiConstants.currencyFromParamName).value(currencyFrom).notBlank().notExceedingLengthOf(3);
        }

        if (this.fromApiJsonHelper.parameterExists(ForexExchangeApiConstants.currencyToParamName, element)) {
            final String currencyTo = this.fromApiJsonHelper.extractStringNamed(ForexExchangeApiConstants.currencyToParamName, element);
            baseDataValidator.reset().parameter(ForexExchangeApiConstants.currencyToParamName).value(currencyTo).notBlank().notExceedingLengthOf(3);
        }

        if (this.fromApiJsonHelper.parameterExists(ForexExchangeApiConstants.clientIdParamName, element)) {
            final String clientId = this.fromApiJsonHelper.extractStringNamed(ForexExchangeApiConstants.clientIdParamName, element);
            baseDataValidator.reset().parameter(ForexExchangeApiConstants.clientIdParamName).value(clientId).ignoreIfNull().notExceedingLengthOf(50);
        }

        if (this.fromApiJsonHelper.parameterExists(ForexExchangeApiConstants.clientNameParamName, element)) {
            final String clientName = this.fromApiJsonHelper.extractStringNamed(ForexExchangeApiConstants.clientNameParamName, element);
            baseDataValidator.reset().parameter(ForexExchangeApiConstants.clientNameParamName).value(clientName).ignoreIfNull().notExceedingLengthOf(50);
        }

        if (this.fromApiJsonHelper.parameterExists(ForexExchangeApiConstants.transactionDateParamName, element)) {
            final LocalDate transactionDate = this.fromApiJsonHelper.extractLocalDateNamed(ForexExchangeApiConstants.transactionDateParamName, element);
            baseDataValidator.reset().parameter(ForexExchangeApiConstants.transactionDateParamName).value(transactionDate).notNull();
        }

        if (this.fromApiJsonHelper.parameterExists(ForexExchangeApiConstants.amountParamName, element)) {
            final BigDecimal amount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(ForexExchangeApiConstants.amountParamName, element);
            baseDataValidator.reset().parameter(ForexExchangeApiConstants.amountParamName).value(amount).notNull().positiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists(ForexExchangeApiConstants.localeParamName, element)) {
            final String locale = this.fromApiJsonHelper.extractStringNamed(ForexExchangeApiConstants.localeParamName, element);
            baseDataValidator.reset().parameter(ForexExchangeApiConstants.localeParamName).value(locale).notBlank();
        }

        if (this.fromApiJsonHelper.parameterExists(ForexExchangeApiConstants.dateFormatParamName, element)) {
            final String dateFormat = this.fromApiJsonHelper.extractStringNamed(ForexExchangeApiConstants.dateFormatParamName, element);
            baseDataValidator.reset().parameter(ForexExchangeApiConstants.dateFormatParamName).value(dateFormat).notBlank();
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) {
            throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                    "Validation errors exist.", dataValidationErrors);
        }
    }
}