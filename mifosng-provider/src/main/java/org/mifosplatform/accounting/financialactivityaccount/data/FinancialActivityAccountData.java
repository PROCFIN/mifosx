/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.financialactivityaccount.data;

import org.mifosplatform.accounting.glaccount.data.GLAccountData;
import org.mifosplatform.organisation.monetary.data.CurrencyData;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class FinancialActivityAccountData {

    private final Long id;
    private final FinancialActivityData financialActivityData;
    private final GLAccountData glAccountData;
    private final String currency;
    private Map<String, List<GLAccountData>> glAccountOptions;
    private List<FinancialActivityData> financialActivityOptions;
    private Collection<CurrencyData> currencyOptions;

    public FinancialActivityAccountData() {
        this.id = null;
        this.glAccountData = null;
        this.financialActivityData = null;
        this.glAccountOptions = null;
        this.currency = null;
        this.financialActivityOptions = null;
        this.currencyOptions = null;
    }

    public FinancialActivityAccountData(final Long id, final FinancialActivityData financialActivityData, final GLAccountData glAccountData, String currency) {
        this.id = id;
        this.glAccountData = glAccountData;
        this.financialActivityData = financialActivityData;
        this.currency = currency;
    }

    public FinancialActivityAccountData(Map<String, List<GLAccountData>> glAccountOptions,
                                        List<FinancialActivityData> financialActivityOptions, Collection<CurrencyData> currencyOptions) {
        this.id = null;
        this.glAccountData = null;
        this.financialActivityData = null;
        this.currency = null;
        this.glAccountOptions = glAccountOptions;
        this.financialActivityOptions = financialActivityOptions;
        this.currencyOptions = currencyOptions;
    }

    public List<FinancialActivityData> getFinancialActivityOptions() {
        return financialActivityOptions;
    }

    public void setFinancialActivityOptions(List<FinancialActivityData> financialActivityOptions) {
        this.financialActivityOptions = financialActivityOptions;
    }

    public Map<String, List<GLAccountData>> getAccountingMappingOptions() {
        return this.glAccountOptions;
    }

    public void setAccountingMappingOptions(Map<String, List<GLAccountData>> accountingMappingOptions) {
        this.glAccountOptions = accountingMappingOptions;
    }

    public GLAccountData getGlAccountData() {
        return glAccountData;
    }

    public FinancialActivityData getFinancialActivityData() {
        return financialActivityData;
    }

    public Long getId() {
        return id;
    }

    public Collection<CurrencyData> getCurrencyOptions() {
        return currencyOptions;
    }

    public void setCurrencyOptions(Collection<CurrencyData> currencyOptions) {
        this.currencyOptions = currencyOptions;
    }
}
