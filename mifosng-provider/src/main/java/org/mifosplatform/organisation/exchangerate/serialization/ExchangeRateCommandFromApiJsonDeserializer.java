/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.exchangerate.serialization;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public final class ExchangeRateCommandFromApiJsonDeserializer {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public ExchangeRateCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {
        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {
        }.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, ExchangeRateApiConstants.EXCHANGE_RATE_CREATE_OR_UPDATE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource(ExchangeRateApiConstants.EXCHANGE_RATE_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final String type = this.fromApiJsonHelper.extractStringNamed(ExchangeRateApiConstants.typeParamName, element);
        baseDataValidator.reset().parameter(ExchangeRateApiConstants.typeParamName).value(type).notBlank().notExceedingLengthOf(50);

        final String currency = this.fromApiJsonHelper.extractStringNamed(ExchangeRateApiConstants.currencyParamName, element);
        baseDataValidator.reset().parameter(ExchangeRateApiConstants.currencyParamName).value(currency).notBlank().notExceedingLengthOf(3);

        final BigDecimal amount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(ExchangeRateApiConstants.amountParamName, element);
        baseDataValidator.reset().parameter(ExchangeRateApiConstants.amountParamName).value(amount).notNull().positiveAmount();

        final LocalDate date = this.fromApiJsonHelper.extractLocalDateNamed(ExchangeRateApiConstants.dateParamName, element);
        baseDataValidator.reset().parameter(ExchangeRateApiConstants.dateParamName).value(date).notNull();

        if (this.fromApiJsonHelper.parameterExists(ExchangeRateApiConstants.localeParamName, element)) {
            final String locale = this.fromApiJsonHelper.extractStringNamed(ExchangeRateApiConstants.localeParamName, element);
            baseDataValidator.reset().parameter(ExchangeRateApiConstants.localeParamName).value(locale).notBlank();
        }

        if (this.fromApiJsonHelper.parameterExists(ExchangeRateApiConstants.dateFormatParamName, element)) {
            final String dateFormat = this.fromApiJsonHelper.extractStringNamed(ExchangeRateApiConstants.dateFormatParamName, element);
            baseDataValidator.reset().parameter(ExchangeRateApiConstants.dateFormatParamName).value(dateFormat).notBlank();
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

        if (this.fromApiJsonHelper.parameterExists(ExchangeRateApiConstants.typeParamName, element)) {
            final String type = this.fromApiJsonHelper.extractStringNamed(ExchangeRateApiConstants.typeParamName, element);
            baseDataValidator.reset().parameter(ExchangeRateApiConstants.typeParamName).value(type).notBlank().notExceedingLengthOf(50);
        }

        if (this.fromApiJsonHelper.parameterExists(ExchangeRateApiConstants.currencyParamName, element)) {
            final String currency = this.fromApiJsonHelper.extractStringNamed(ExchangeRateApiConstants.currencyParamName, element);
            baseDataValidator.reset().parameter(ExchangeRateApiConstants.currencyParamName).value(currency).notBlank().notExceedingLengthOf(3);
        }

        if (this.fromApiJsonHelper.parameterExists(ExchangeRateApiConstants.amountParamName, element)) {
            final BigDecimal amount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(ExchangeRateApiConstants.amountParamName, element);
            baseDataValidator.reset().parameter(ExchangeRateApiConstants.amountParamName).value(amount).notNull().positiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists(ExchangeRateApiConstants.dateParamName, element)) {
            final LocalDate date = this.fromApiJsonHelper.extractLocalDateNamed(ExchangeRateApiConstants.dateParamName, element);
            baseDataValidator.reset().parameter(ExchangeRateApiConstants.dateParamName).value(date).notNull();
        }

        if (this.fromApiJsonHelper.parameterExists(ExchangeRateApiConstants.localeParamName, element)) {
            final String locale = this.fromApiJsonHelper.extractStringNamed(ExchangeRateApiConstants.localeParamName, element);
            baseDataValidator.reset().parameter(ExchangeRateApiConstants.localeParamName).value(locale).notBlank();
        }

        if (this.fromApiJsonHelper.parameterExists(ExchangeRateApiConstants.dateFormatParamName, element)) {
            final String dateFormat = this.fromApiJsonHelper.extractStringNamed(ExchangeRateApiConstants.dateFormatParamName, element);
            baseDataValidator.reset().parameter(ExchangeRateApiConstants.dateFormatParamName).value(dateFormat).notBlank();
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