package org.mifosplatform.organisation.exchangerate.domain;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;

import java.util.*;

public enum ExchangeRateType {
    BUYING(1, "exchangeRateType.buying"), SELLING(2, "exchangeRateType.selling"), INTERMEDIARY(3, "exchangeRateType.intermediary");

    private final Integer value;
    private final String code;
    private static Collection<ExchangeRateType> rateTypes;

    private ExchangeRateType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    private static final Map<Integer, ExchangeRateType> intToEnumMap = new HashMap<>();
    private static int minValue;
    private static int maxValue;

    static {
        int i = 0;
        for (final ExchangeRateType type : ExchangeRateType.values()) {
            if (i == 0) {
                minValue = type.value;
            }
            intToEnumMap.put(type.value, type);
            if (minValue >= type.value) {
                minValue = type.value;
            }
            if (maxValue < type.value) {
                maxValue = type.value;
            }
            i = i + 1;
        }
    }

    public static ExchangeRateType fromInt(final int i) {
        final ExchangeRateType type = intToEnumMap.get(Integer.valueOf(i));
        return type;
    }

    public static int getMinValue() {
        return minValue;
    }

    public static int getMaxValue() {
        return maxValue;
    }

    @Override
    public String toString() {
        return name().toString();
    }

    public boolean isBuyingType() {
        return this.value.equals(ExchangeRateType.BUYING.getValue());
    }

    public boolean isSellingType() {
        return this.value.equals(ExchangeRateType.SELLING.getValue());
    }

    public boolean isIntermediaryType() {
        return this.value.equals(ExchangeRateType.INTERMEDIARY.getValue());
    }

    public static List<EnumOptionData> getAllRateTypes() {
        final List<EnumOptionData> optionData = new ArrayList<>();
        final ExchangeRateType[] exchangeRateTypes = ExchangeRateType.values();
        for (final ExchangeRateType exchangeRateType : exchangeRateTypes) {
            optionData.add(exchangeRateType(exchangeRateType));
        }
        return optionData;
    }

    public static EnumOptionData exchangeRateType(final ExchangeRateType accountType) {
        if (accountType == null) {
            return null;
        }
        final EnumOptionData optionData = new EnumOptionData(accountType.getValue().longValue(), accountType.getCode(), accountType.toString());
        return optionData;
    }
}
