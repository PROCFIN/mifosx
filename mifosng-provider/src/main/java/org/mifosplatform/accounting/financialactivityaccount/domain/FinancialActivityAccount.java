/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.financialactivityaccount.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mifosplatform.accounting.glaccount.domain.GLAccount;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "acc_gl_financial_activity_account")
public class FinancialActivityAccount extends AbstractPersistable<Long> {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gl_account_id")
    private GLAccount glAccount;

    @Column(name = "financial_activity_type", nullable = false)
    private Integer financialActivityType;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    public static FinancialActivityAccount createNew(final GLAccount glAccount, final Integer financialAccountType, final String currency) {
        return new FinancialActivityAccount(glAccount, financialAccountType, currency);
    }

    protected FinancialActivityAccount() {
        //
    }

    private FinancialActivityAccount(final GLAccount glAccount, final int financialAccountType, final String currency) {
        this.glAccount = glAccount;
        this.financialActivityType = financialAccountType;
        this.currency = currency;
    }

    public GLAccount getGlAccount() {
        return this.glAccount;
    }

    public Integer getFinancialActivityType() {
        return this.financialActivityType;
    }

    public void updateGlAccount(final GLAccount glAccount) {
        this.glAccount = glAccount;
    }

    public void updateFinancialActivityType(final Integer financialActivityType) {
        this.financialActivityType = financialActivityType;
    }

    public String getCurrency() {
        return currency;
    }

    public void updateCurrency(String currency) {
        this.currency = currency;
    }
}