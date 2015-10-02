/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.forexexchange.service;

import org.joda.time.LocalDate;
import org.mifosplatform.accounting.common.AccountingConstants;
import org.mifosplatform.accounting.financialactivityaccount.domain.FinancialActivityAccount;
import org.mifosplatform.accounting.financialactivityaccount.domain.FinancialActivityAccountRepositoryWrapper;
import org.mifosplatform.accounting.glaccount.domain.GLAccount;
import org.mifosplatform.accounting.journalentry.domain.JournalEntry;
import org.mifosplatform.accounting.journalentry.domain.JournalEntryRepository;
import org.mifosplatform.accounting.journalentry.domain.JournalEntryType;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.exchangerate.domain.ExchangeRate;
import org.mifosplatform.organisation.exchangerate.domain.ExchangeRateRepositoryWrapper;
import org.mifosplatform.organisation.exchangerate.domain.ExchangeRateType;
import org.mifosplatform.organisation.forexexchange.api.ForexExchangeApiConstants;
import org.mifosplatform.organisation.forexexchange.domain.ForexExchange;
import org.mifosplatform.organisation.forexexchange.domain.ForexExchangeRepository;
import org.mifosplatform.organisation.forexexchange.exception.ForexExchangeNotFoundException;
import org.mifosplatform.organisation.forexexchange.serialization.ForexExchangeCommandFromApiJsonDeserializer;
import org.mifosplatform.organisation.office.domain.Office;
import org.mifosplatform.organisation.office.domain.OrganisationCurrency;
import org.mifosplatform.organisation.office.domain.OrganisationCurrencyRepositoryWrapper;
import org.mifosplatform.organisation.teller.domain.*;
import org.mifosplatform.useradministration.domain.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
public class ForexExchangeWritePlatformServiceJpaRepositoryImpl implements ForexExchangeWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(ForexExchangeWritePlatformServiceJpaRepositoryImpl.class);

    private final ForexExchangeCommandFromApiJsonDeserializer fromApiJsonDeserializer;
    private final ForexExchangeRepository forexExchangeRepository;
    private final PlatformSecurityContext context;
    private final ExchangeRateRepositoryWrapper exchangeRateRepositoryWrapper;
    private final OrganisationCurrencyRepositoryWrapper organisationCurrencyRepositoryWrapper;
    private final CashierRepositoryWrapper cashierRepositoryWrapper;
    private final CashierTransactionRepository cashierTransactionRepository;
    private final FinancialActivityAccountRepositoryWrapper financialActivityAccountRepositoryWrapper;
    private final JournalEntryRepository journalEntryRepository;

    @Autowired
    public ForexExchangeWritePlatformServiceJpaRepositoryImpl(final ForexExchangeCommandFromApiJsonDeserializer fromApiJsonDeserializer,
                                                              final ForexExchangeRepository forexExchangeRepository,
                                                              final PlatformSecurityContext context,
                                                              final ExchangeRateRepositoryWrapper exchangeRateRepositoryWrapper,
                                                              final OrganisationCurrencyRepositoryWrapper organisationCurrencyRepositoryWrapper,
                                                              final CashierRepositoryWrapper cashierRepositoryWrapper,
                                                              final CashierTransactionRepository cashierTransactionRepository,
                                                              final FinancialActivityAccountRepositoryWrapper financialActivityAccountRepositoryWrapper,
                                                              final JournalEntryRepository journalEntryRepository) {
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.forexExchangeRepository = forexExchangeRepository;
        this.context = context;
        this.exchangeRateRepositoryWrapper = exchangeRateRepositoryWrapper;
        this.organisationCurrencyRepositoryWrapper = organisationCurrencyRepositoryWrapper;
        this.cashierRepositoryWrapper = cashierRepositoryWrapper;
        this.cashierTransactionRepository = cashierTransactionRepository;
        this.financialActivityAccountRepositoryWrapper = financialActivityAccountRepositoryWrapper;
        this.journalEntryRepository = journalEntryRepository;
    }

    @Transactional
    @Override
    public CommandProcessingResult createForexExchange(final JsonCommand command) {

        try {
            final AppUser currentUser = this.context.authenticatedUser();

            this.fromApiJsonDeserializer.validateForCreate(command.json());

            final String currencyFromCode = command.stringValueOfParameterNamed(ForexExchangeApiConstants.currencyFromParamName);
            final OrganisationCurrency currencyFrom = this.organisationCurrencyRepositoryWrapper.findOneWithNotFoundDetection(currencyFromCode);

            final String currencyToCode = command.stringValueOfParameterNamed(ForexExchangeApiConstants.currencyToParamName);
            final OrganisationCurrency currencyTo = this.organisationCurrencyRepositoryWrapper.findOneWithNotFoundDetection(currencyToCode);

            final BigDecimal amount = command.bigDecimalValueOfParameterNamed(ForexExchangeApiConstants.amountParamName);
            final LocalDate transactionDate = command.localDateValueOfParameterNamed(ForexExchangeApiConstants.transactionDateParamName);
            //for example home currency = UGX
            if (currencyTo.isHomeCurrency()) {// USD -> UGX
                final ExchangeRate exchangeRate = this.exchangeRateRepositoryWrapper.findOneByCurrencyAndTypeBeforeDateWithNotFoundDetection(currencyFrom.getCode(), ExchangeRateType.BUYING.getValue(), transactionDate);
                final ForexExchange forexExchange = ForexExchange.fromJson(command, exchangeRate, currentUser, currencyFrom, currencyTo);
                forexExchange.setAmountGiven(amount);
                forexExchange.setAmountTaken(amount.multiply(exchangeRate.getAmount()));
                this.forexExchangeRepository.save(forexExchange);
                createTransacrions(forexExchange);
            } else if (currencyFrom.isHomeCurrency()) {// UGX -> USD
                final ExchangeRate exchangeRate = this.exchangeRateRepositoryWrapper.findOneByCurrencyAndTypeBeforeDateWithNotFoundDetection(currencyTo.getCode(), ExchangeRateType.SELLING.getValue(), transactionDate);
                final ForexExchange forexExchange = ForexExchange.fromJson(command, exchangeRate, currentUser, currencyFrom, currencyTo);
                forexExchange.setAmountGiven(amount);
                forexExchange.setAmountTaken(amount.divide(exchangeRate.getAmount(), 6, RoundingMode.HALF_EVEN));
                this.forexExchangeRepository.save(forexExchange);
                createTransacrions(forexExchange);
            } else {// USD -> UGX -> EUR
                final OrganisationCurrency homeCurrency = this.organisationCurrencyRepositoryWrapper.findHomeCurrencyNotFoundDetection();

                final ExchangeRate exchangeRateToHomeCurrency = this.exchangeRateRepositoryWrapper.findOneByCurrencyAndTypeBeforeDateWithNotFoundDetection(currencyFrom.getCode(), ExchangeRateType.BUYING.getValue(), transactionDate);
                final ForexExchange forexExchangeToHomeCurrency = ForexExchange.fromJson(command, exchangeRateToHomeCurrency, currentUser, currencyFrom, homeCurrency);
                forexExchangeToHomeCurrency.setAmountGiven(amount);
                final BigDecimal exchangedAmount = amount.multiply(exchangeRateToHomeCurrency.getAmount());
                forexExchangeToHomeCurrency.setAmountTaken(exchangedAmount);
                this.forexExchangeRepository.save(forexExchangeToHomeCurrency);
                createTransacrions(forexExchangeToHomeCurrency);

                final ExchangeRate exchangeRateFromHomeCurrency = this.exchangeRateRepositoryWrapper.findOneByCurrencyAndTypeBeforeDateWithNotFoundDetection(currencyTo.getCode(), ExchangeRateType.SELLING.getValue(), transactionDate);
                final ForexExchange forexExchangeFromHomeCurrency = ForexExchange.fromJson(command, exchangeRateFromHomeCurrency, currentUser, homeCurrency, currencyTo);
                forexExchangeFromHomeCurrency.setAmountGiven(exchangedAmount);
                forexExchangeFromHomeCurrency.setAmountTaken(exchangedAmount.divide(exchangeRateFromHomeCurrency.getAmount(), 6, RoundingMode.HALF_EVEN));
                this.forexExchangeRepository.save(forexExchangeFromHomeCurrency);
                createTransacrions(forexExchangeFromHomeCurrency);
            }


            return new CommandProcessingResultBuilder()
                    .withCommandId(command.commandId())
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleStaffDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    private void createTransacrions(ForexExchange forexExchange) {
        AppUser currentUser = forexExchange.getUser();

        Cashier cashier = this.cashierRepositoryWrapper.findOneByStaffWithNotFoundDetection(currentUser.getStaffId());
        saveJournalEntry(forexExchange, cashier, CashierTxnType.OUTWARD_CASH_TXN);
        saveJournalEntry(forexExchange, cashier, CashierTxnType.INWARD_CASH_TXN);
    }

    private void saveJournalEntry(ForexExchange forexExchange, Cashier cashier, CashierTxnType txnType) {

        final CashierTransaction cashierTransaction;
        if (txnType.equals(CashierTxnType.OUTWARD_CASH_TXN)) {
            BigDecimal txnAmount = forexExchange.getAmountGiven();
            LocalDate txnDate = LocalDate.fromDateFields(forexExchange.getTransactionDate());
            String entityType = null;
            Long entityId = null;
            String txnNote = forexExchange.getClientName();
            String currencyCode = forexExchange.getCurrencyFrom().getCode();
            cashierTransaction = new CashierTransaction(cashier, txnType.getId(), txnAmount, txnDate, entityType, entityId, txnNote, currencyCode);
        } else {
            BigDecimal txnAmount = forexExchange.getAmountTaken();
            LocalDate txnDate = LocalDate.fromDateFields(forexExchange.getTransactionDate());
            String entityType = null;
            Long entityId = null;
            String txnNote = forexExchange.getClientName();
            String currencyCode = forexExchange.getCurrencyTo().getCode();
            cashierTransaction = new CashierTransaction(cashier, txnType.getId(), txnAmount, txnDate, entityType, entityId, txnNote, currencyCode);
        }

        this.cashierTransactionRepository.save(cashierTransaction);

        // Pass the journal entries
        FinancialActivityAccount mainVaultFinancialActivityAccount = this.financialActivityAccountRepositoryWrapper
                .findByFinancialActivityTypeAndCurrencyWithNotFoundDetection(AccountingConstants.FINANCIAL_ACTIVITY.CASH_AT_MAINVAULT.getValue(), cashierTransaction.getCurrencyCode());
        FinancialActivityAccount tellerCashFinancialActivityAccount = this.financialActivityAccountRepositoryWrapper
                .findByFinancialActivityTypeAndCurrencyWithNotFoundDetection(AccountingConstants.FINANCIAL_ACTIVITY.CASH_AT_TELLER.getValue(), cashierTransaction.getCurrencyCode());
        GLAccount creditAccount = null;
        GLAccount debitAccount = null;
        if (txnType.equals(CashierTxnType.INWARD_CASH_TXN)) {
            debitAccount = tellerCashFinancialActivityAccount.getGlAccount();
            creditAccount = mainVaultFinancialActivityAccount.getGlAccount();
        } else if (txnType.equals(CashierTxnType.OUTWARD_CASH_TXN)) {
            debitAccount = mainVaultFinancialActivityAccount.getGlAccount();
            creditAccount = tellerCashFinancialActivityAccount.getGlAccount();
        }

        final Office cashierOffice = cashier.getTeller().getOffice();

        final Long time = System.currentTimeMillis();
        final String uniqueVal = String.valueOf(time) + forexExchange.getUser().getId() + cashierOffice.getId();
        final String transactionId = Long.toHexString(Long.parseLong(uniqueVal));

//        ExchangeRate exchangeRate = this.exchangeRateRepositoryWrapper.findOneByCurrencyAndTypeBeforeTodayWithNotFoundDetection(cashierTransaction.getCurrencyCode(), ExchangeRateType.INTERMEDIARY.getValue());
        ExchangeRate exchangeRate = forexExchange.getExchangeRate();

        final JournalEntry debitJournalEntry = JournalEntry.createNew(cashierOffice, null, // payment detail
                debitAccount, cashierTransaction.getCurrencyCode(), exchangeRate,
                transactionId, false, // manual entry
                cashierTransaction.getTxnDate(), JournalEntryType.DEBIT, cashierTransaction.getTxnAmount(), cashierTransaction.getTxnNote(), // Description
                null, null, null, // entity Type, entityId, reference number
                null, null); // Loan and Savings Txn
        final JournalEntry creditJournalEntry = JournalEntry.createNew(cashierOffice, null, // payment detail
                creditAccount, cashierTransaction.getCurrencyCode(), exchangeRate,
                transactionId, false, // manual entry
                cashierTransaction.getTxnDate(), JournalEntryType.CREDIT, cashierTransaction.getTxnAmount(), cashierTransaction.getTxnNote(), // Description
                null, null, null, // entity Type, entityId, reference number
                null, null); // Loan and Savings Txn

        this.journalEntryRepository.saveAndFlush(debitJournalEntry);
        this.journalEntryRepository.saveAndFlush(creditJournalEntry);
    }


    @Transactional
    @Override
    public CommandProcessingResult updateForexExchange(final Long forexExchangeId, final JsonCommand command) {

        try {
            this.fromApiJsonDeserializer.validateForUpdate(command.json());

            final ForexExchange forexExchangeForUpdate = this.forexExchangeRepository.findOne(forexExchangeId);
            if (forexExchangeForUpdate == null) {
                throw new ForexExchangeNotFoundException(forexExchangeId);
            }

            final Map<String, Object> changesOnly = forexExchangeForUpdate.update(command);

            if (changesOnly.containsKey(ForexExchangeApiConstants.exchangeRateIdParamName)) {
                final Long exchangeRateId = (Long) changesOnly.get(ForexExchangeApiConstants.exchangeRateIdParamName);
                ExchangeRate exchangeRate = this.exchangeRateRepositoryWrapper.findOneWithNotFoundDetection(exchangeRateId);
                forexExchangeForUpdate.setExchangeRate(exchangeRate);
            }

            if (changesOnly.containsKey(ForexExchangeApiConstants.currencyFromParamName)) {
                final String currencyFromCode = (String) changesOnly.get(ForexExchangeApiConstants.currencyFromParamName);
                final OrganisationCurrency currencyFrom = this.organisationCurrencyRepositoryWrapper.findOneWithNotFoundDetection(currencyFromCode);
                forexExchangeForUpdate.setCurrencyFrom(currencyFrom);
            }

            if (changesOnly.containsKey(ForexExchangeApiConstants.currencyToParamName)) {
                final String currencyToCode = (String) changesOnly.get(ForexExchangeApiConstants.currencyToParamName);
                final OrganisationCurrency currencyTo = this.organisationCurrencyRepositoryWrapper.findOneWithNotFoundDetection(currencyToCode);
                forexExchangeForUpdate.setCurrencyFrom(currencyTo);
            }

            if (!changesOnly.isEmpty()) {
                this.forexExchangeRepository.saveAndFlush(forexExchangeForUpdate);
            }

            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(forexExchangeId)
                    .with(changesOnly).build();
        } catch (final DataIntegrityViolationException dve) {
            handleStaffDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult deleteForexExchange(final Long forexExchangeId) {
        final ForexExchange forexExchange = this.forexExchangeRepository.findOne(forexExchangeId);

        if (forexExchange == null) {
            throw new ForexExchangeNotFoundException(forexExchangeId);
        }

        this.forexExchangeRepository.delete(forexExchange);

        return new CommandProcessingResultBuilder().withEntityId(forexExchangeId).build();
    }

    /*
     * Guaranteed to throw an exception no matter what the data integrity issue is.
     */
    private void handleStaffDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {
        final Throwable realCause = dve.getMostSpecificCause();

        logger.error(dve.getMessage(), dve);
        throw new PlatformDataIntegrityException("error.msg.forex.exchange.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource: " + realCause.getMessage());
    }
}