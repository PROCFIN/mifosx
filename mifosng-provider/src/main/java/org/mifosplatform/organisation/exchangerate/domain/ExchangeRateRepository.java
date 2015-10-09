/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.exchangerate.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long>, JpaSpecificationExecutor<ExchangeRate> {

    @Query(nativeQuery = true, value = "select er.* from m_exchange_rate er where er.currency = :currency AND type = :type AND date <= :date order by date desc limit 1")
    ExchangeRate findByCurrencyAndTypeAndDateBefore(@Param("currency") String currency, @Param("type") int type, @Param("date") Date date);

}