/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.exchangerate.service;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.exchangerate.data.ExchangeRateData;
import org.mifosplatform.organisation.staff.exception.StaffNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Service
public class ExchangeRatePlatformServiceImpl implements ExchangeRatePlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
//    private final ExchangeRateLookupMapper lookupMapper = new ExchangeRateLookupMapper();

    @Autowired
    public ExchangeRatePlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class ExchangeRateMapper implements RowMapper<ExchangeRateData> {

        public String schema() {
            return " er.id as id, er.date as date, cv.id as typeId, cv.code_value as typeValue, er.currency as currency, er.amount as amount from m_exchange_rate er "
                    + " left join m_organisation_currency oc on oc.code = er.currency"
                    + " left join m_code_value cv on cv.id = er.type_cv_id"
                    + " left join m_currency c on c.code = er.currency";
        }

        @Override
        public ExchangeRateData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");

            final String currency = rs.getString("currency");
            final LocalDate date = JdbcSupport.getLocalDate(rs, "date");
            final BigDecimal amount = rs.getBigDecimal("amount");

            final Long typeId = rs.getLong("typeId");
            final String typeValue = rs.getString("typeValue");
            final CodeValueData rateType = CodeValueData.instance(typeId, typeValue);

            return ExchangeRateData.instance(id, date, rateType, currency, amount);
        }
    }

    @Override
    public ExchangeRateData retrieveExchangeRate(final Long exchangeRateId) {

        try {
            final ExchangeRateMapper rm = new ExchangeRateMapper();
            final String sql = "select " + rm.schema() + " where er.id = ?";

            return this.jdbcTemplate.queryForObject(sql, rm, new Object[]{exchangeRateId});
        } catch (final EmptyResultDataAccessException e) {
            throw new StaffNotFoundException(exchangeRateId);
        }
    }

    @Override
    public Collection<ExchangeRateData> retrieveAllExchangeRates(final String sqlSearch) {
        final String extraCriteria = getExchangeRateCriteria(sqlSearch);
        final ExchangeRateMapper rm = new ExchangeRateMapper();
        String sql = "select " + rm.schema();
        if (StringUtils.isNotBlank(extraCriteria)) {
            sql += " where " + extraCriteria;
        }
        sql = sql + " order by er.currency";
        return this.jdbcTemplate.query(sql, rm, new Object[]{});
    }

    private String getExchangeRateCriteria(final String sqlSearch) {

        final StringBuffer extraCriteria = new StringBuffer(200);

        if (sqlSearch != null) {
            extraCriteria.append(" and (").append(sqlSearch).append(")");
        }

        if (StringUtils.isNotBlank(extraCriteria.toString())) {
            extraCriteria.delete(0, 4);
        }

        // remove begin four letter including a space from the string.
        return extraCriteria.toString();
    }
}