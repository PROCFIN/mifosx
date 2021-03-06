/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.office.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Represents currencies allowed for this MFI/organisation.
 */
@Entity
@Table(name = "m_organisation_currency")
public class OrganisationCurrency extends AbstractPersistable<Long> {

    @Column(name = "code", nullable = false, length = 3)
    private final String code;

    @Column(name = "decimal_places", nullable = false)
    private final Integer decimalPlaces;

    @Column(name = "currency_multiplesof")
    private final Integer inMultiplesOf;

    @Column(name = "name", nullable = false, length = 50)
    private final String name;

    @Column(name = "internationalized_name_code", nullable = false, length = 50)
    private final String nameCode;

    @Column(name = "display_symbol", nullable = true, length = 10)
    private final String displaySymbol;

    @Column(name = "is_home_currency", nullable = true)
    private final boolean isHomeCurrency;

    protected OrganisationCurrency() {
        this.code = null;
        this.name = null;
        this.decimalPlaces = null;
        this.inMultiplesOf = null;
        this.nameCode = null;
        this.displaySymbol = null;
        this.isHomeCurrency = false;
    }

    public OrganisationCurrency(final String code, final String name, final int decimalPlaces, final Integer inMultiplesOf,
                                final String nameCode, final String displaySymbol, final boolean isHomeCurrency) {
        this.code = code;
        this.name = name;
        this.decimalPlaces = decimalPlaces;
        this.inMultiplesOf = inMultiplesOf;
        this.nameCode = nameCode;
        this.displaySymbol = displaySymbol;
        this.isHomeCurrency = isHomeCurrency;
    }

    public final String getCode() {
        return code;
    }

    public boolean isHomeCurrency() {
        return isHomeCurrency;
    }
}