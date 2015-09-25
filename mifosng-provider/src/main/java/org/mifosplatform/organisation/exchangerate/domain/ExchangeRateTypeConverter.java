package org.mifosplatform.organisation.exchangerate.domain;

import javax.persistence.AttributeConverter;

public class ExchangeRateTypeConverter implements AttributeConverter<ExchangeRateType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ExchangeRateType exchangeRateType) {
        return exchangeRateType.getValue();
    }

    @Override
    public ExchangeRateType convertToEntityAttribute(Integer exchangeRateTypeId) {
        return ExchangeRateType.fromInt(exchangeRateTypeId);
    }
}
