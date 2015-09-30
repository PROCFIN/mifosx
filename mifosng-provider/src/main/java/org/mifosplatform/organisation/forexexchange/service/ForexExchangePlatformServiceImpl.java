/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.forexexchange.service;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.exchangerate.data.ExchangeRateData;
import org.mifosplatform.organisation.exchangerate.domain.ExchangeRateType;
import org.mifosplatform.organisation.forexexchange.data.ForexExchangeData;
import org.mifosplatform.organisation.forexexchange.exception.ForexExchangeNotFoundException;
import org.mifosplatform.organisation.monetary.data.CurrencyData;
import org.mifosplatform.useradministration.service.AppUserReadPlatformService;
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
public class ForexExchangePlatformServiceImpl implements ForexExchangePlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;

    @Autowired
    public ForexExchangePlatformServiceImpl(final PlatformSecurityContext context,
                                            final RoutingDataSource dataSource,
                                            AppUserReadPlatformService appUserReadPlatformService) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class ForexExchangeMapper implements RowMapper<ForexExchangeData> {

        public String schema() {
            StringBuilder sql = new StringBuilder();
            sql.append(" fe.id as id, fe.client_id as clientId, fe.client_name as clientName, fe.transaction_date as transactionDate, fe.created_date as createdDate, fe.amount_given as amountGiven, fe.amount_taken as amountTaken, ")
                    .append("au.username as createdByUserName, au.id as createdById, ")
                    .append("er.id as exchangeRateId, er.date as exchangeRateDate, er.type as exchangeRateTypeId, er.currency as exchangeRateCurrency, er.amount as exchangeRateAmount, ")
                    .append("ocf.code as currencyCodeFrom, ocf.decimal_places as currencyDecimalPlacesFrom, ocf.currency_multiplesof as inMultiplesOfFrom, ocf.`name` as currencyNameFrom, ocf.display_symbol as currencyDisplaySymbolFrom, ocf.internationalized_name_code as currencyNameCodeFrom, ")
                    .append("oct.code as currencyCodeTo, oct.decimal_places as currencyDecimalPlacesTo, oct.currency_multiplesof as inMultiplesOfTo, oct.`name` as currencyNameTo, oct.display_symbol as currencyDisplaySymbolTo, oct.internationalized_name_code as currencyNameCodeTo ")
                    .append(" from m_forex_exchange fe")
                    .append(" left join m_organisation_currency ocf on ocf.code = fe.currency_from_code")
                    .append(" left join m_organisation_currency oct on oct.code = fe.currency_to_code")
                    .append(" left join m_exchange_rate er on er.id = fe.exchange_rate_id")
                    .append(" left join m_appuser au on au.id = fe.createdby_id")
                    .append(" left join m_staff s on s.id = au.staff_id")
                    .append(" left join m_currency c on c.code = er.currency ");
            return sql.toString();
        }

        @Override
        public ForexExchangeData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");

            //Exchange Rate Data
            final Long exchangeRateId = rs.getLong("exchangeRateId");
            final LocalDate exchangeRateDate = JdbcSupport.getLocalDate(rs, "exchangeRateDate");
            final Integer exchangeRateTypeId = JdbcSupport.getInteger(rs, "exchangeRateTypeId");
            final String exchangeRateCurrency = rs.getString("exchangeRateCurrency");
            final BigDecimal exchangeRateAmount = rs.getBigDecimal("exchangeRateAmount");
            final ExchangeRateType exchangeRateType = ExchangeRateType.fromInt(exchangeRateTypeId);
            final ExchangeRateData exchangeRateData = ExchangeRateData.instance(exchangeRateId, exchangeRateDate, ExchangeRateType.exchangeRateType(exchangeRateType), exchangeRateCurrency, exchangeRateAmount);

            //Currency From Data
            final String currencyCodeFrom = rs.getString("currencyCodeFrom");
            final String currencyNameFrom = rs.getString("currencyNameFrom");
            final String currencyDisplaySymbolFrom = rs.getString("currencyDisplaySymbolFrom");
            final String currencyNameCodeFrom = rs.getString("currencyNameCodeFrom");
            final Integer currencyDecimalPlacesFrom = JdbcSupport.getInteger(rs, "currencyDecimalPlacesFrom");
            final Integer inMultiplesOfFrom = JdbcSupport.getInteger(rs, "inMultiplesOfFrom");
            final CurrencyData currencyDataFrom = new CurrencyData(currencyCodeFrom, currencyNameFrom, currencyDecimalPlacesFrom, inMultiplesOfFrom, currencyDisplaySymbolFrom, currencyNameCodeFrom);

            //Currency To Data
            final String currencyCodeTo = rs.getString("currencyCodeTo");
            final String currencyNameTo = rs.getString("currencyNameTo");
            final String currencyDisplaySymbolTo = rs.getString("currencyDisplaySymbolTo");
            final String currencyNameCodeTo = rs.getString("currencyNameCodeTo");
            final Integer currencyDecimalPlacesTo = JdbcSupport.getInteger(rs, "currencyDecimalPlacesTo");
            final Integer inMultiplesOfTo = JdbcSupport.getInteger(rs, "inMultiplesOfTo");
            final CurrencyData currencyDataTo = new CurrencyData(currencyCodeTo, currencyNameTo, currencyDecimalPlacesTo, inMultiplesOfTo, currencyDisplaySymbolTo, currencyNameCodeTo);


            //Forex Exchange Data
            final Long createdById = rs.getLong("createdById");
            final String createdByUserName = rs.getString("createdByUserName");
            final String clientId = rs.getString("clientId");
            final String clientName = rs.getString("clientName");
            final LocalDate transactionDate = JdbcSupport.getLocalDate(rs, "transactionDate");
            final LocalDate createdDate = JdbcSupport.getLocalDate(rs, "createdDate");
            final BigDecimal amountGiven = rs.getBigDecimal("amountGiven");
            final BigDecimal amountTaken = rs.getBigDecimal("amountTaken");

            return ForexExchangeData.instance(id, exchangeRateData, createdById, createdByUserName, currencyDataFrom, currencyDataTo, clientId, clientName, transactionDate, createdDate, amountGiven, amountTaken);
        }
    }

    @Override
    public ForexExchangeData retrieveForexExchange(final Long forexExchangeId) {

        try {
            final ForexExchangeMapper rm = new ForexExchangeMapper();
            final String sql = "select " + rm.schema() + " where fe.id = ?";

            return this.jdbcTemplate.queryForObject(sql, rm, new Object[]{forexExchangeId});
        } catch (final EmptyResultDataAccessException e) {
            throw new ForexExchangeNotFoundException(forexExchangeId);
        }
    }

    @Override
    public Collection<ForexExchangeData> retrieveAllForexExchanges(final String sqlSearch) {
        final String extraCriteria = getExchangeRateCriteria(sqlSearch, null, null);
        final ForexExchangeMapper rm = new ForexExchangeMapper();
        String sql = "select " + rm.schema();
        if (StringUtils.isNotBlank(extraCriteria)) {
            sql += " where " + extraCriteria;
        }
        sql = sql + " order by fe.created_date";
        return this.jdbcTemplate.query(sql, rm, new Object[]{});
    }

    @Override
    public Collection<ForexExchangeData> retrieveCashierTransactions(Long staffId, String currencyCode) {
        final String extraCriteria = getExchangeRateCriteria(null, staffId, currencyCode);
        final ForexExchangeMapper rm = new ForexExchangeMapper();
        String sql = "select " + rm.schema();
        if (StringUtils.isNotBlank(extraCriteria)) {
            sql += " where " + extraCriteria;
        }
        sql = sql + " order by fe.created_date";
        return this.jdbcTemplate.query(sql, rm, new Object[]{});
    }

    private String getExchangeRateCriteria(final String sqlSearch, final Long staffId, final String currencyCode) {

        final StringBuffer extraCriteria = new StringBuffer(200);

        if (sqlSearch != null) {
            extraCriteria.append(" and (").append(sqlSearch).append(")");
        }
        if (staffId != null) {
            extraCriteria.append(" and s.id = ").append(staffId);
        }
        if (currencyCode != null) {
            extraCriteria.append(" and (ocf.code = '").append(currencyCode).append("' or oct.code = '").append(currencyCode).append("')");
        }
        if (StringUtils.isNotBlank(extraCriteria.toString())) {
            extraCriteria.delete(0, 4);
        }

        // remove begin four letter including a space from the string.
        return extraCriteria.toString();
    }
}