/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.exchangerate.service;

import org.mifosplatform.infrastructure.codes.domain.CodeValue;
import org.mifosplatform.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.organisation.exchangerate.api.ExchangeRateApiConstants;
import org.mifosplatform.organisation.exchangerate.domain.ExchangeRate;
import org.mifosplatform.organisation.exchangerate.domain.ExchangeRateRepository;
import org.mifosplatform.organisation.exchangerate.exception.ExchangeRateNotFoundException;
import org.mifosplatform.organisation.exchangerate.serialization.ExchangeRateCommandFromApiJsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class ExchangeRateWritePlatformServiceJpaRepositoryImpl implements ExchangeRateWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(ExchangeRateWritePlatformServiceJpaRepositoryImpl.class);

    private final ExchangeRateCommandFromApiJsonDeserializer fromApiJsonDeserializer;
    private final ExchangeRateRepository exchangeRateRepository;
    private final CodeValueRepositoryWrapper codeValueRepositoryWrapper;

    @Autowired
    public ExchangeRateWritePlatformServiceJpaRepositoryImpl(final ExchangeRateCommandFromApiJsonDeserializer fromApiJsonDeserializer,
                                                             final ExchangeRateRepository exchangeRateRepository,
                                                             final CodeValueRepositoryWrapper codeValueRepositoryWrapper) {
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.exchangeRateRepository = exchangeRateRepository;
        this.codeValueRepositoryWrapper = codeValueRepositoryWrapper;
    }

    @Transactional
    @Override
    public CommandProcessingResult createExchangeRate(final JsonCommand command) {

        try {
            final Long typeId = command.longValueOfParameterNamed(ExchangeRateApiConstants.typeParamName);

            this.fromApiJsonDeserializer.validateForCreate(command.json());
            CodeValue rateType = this.codeValueRepositoryWrapper.findOneByCodeNameAndIdWithNotFoundDetection(ExchangeRateApiConstants.TYPE_OPTION_CODE_NAME, typeId);
            final ExchangeRate exchangeRate = ExchangeRate.fromJson(command, rateType);

            this.exchangeRateRepository.save(exchangeRate);

            return new CommandProcessingResultBuilder()
                    .withCommandId(command.commandId())
                    .withEntityId(exchangeRate.getId())
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleStaffDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult updateExchangeRate(final Long exchangeRateId, final JsonCommand command) {

        try {
            this.fromApiJsonDeserializer.validateForUpdate(command.json());

            final ExchangeRate exchangeRateForUpdate = this.exchangeRateRepository.findOne(exchangeRateId);
            if (exchangeRateForUpdate == null) {
                throw new ExchangeRateNotFoundException(exchangeRateId);
            }

            final Map<String, Object> changesOnly = exchangeRateForUpdate.update(command);

            if (changesOnly.containsKey(ExchangeRateApiConstants.typeParamName)) {
                final Long typeIdLongValue = command.longValueOfParameterNamed(ExchangeRateApiConstants.typeParamName);
                CodeValue rateType = null;
                if (typeIdLongValue != null) {
                    rateType = this.codeValueRepositoryWrapper.findOneByCodeNameAndIdWithNotFoundDetection(ExchangeRateApiConstants.TYPE_OPTION_CODE_NAME, typeIdLongValue);
                }
                exchangeRateForUpdate.setRateType(rateType);
            }

            if (!changesOnly.isEmpty()) {
                this.exchangeRateRepository.saveAndFlush(exchangeRateForUpdate);
            }

            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(exchangeRateId)
                    .with(changesOnly).build();
        } catch (final DataIntegrityViolationException dve) {
            handleStaffDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    /*
     * Guaranteed to throw an exception no matter what the data integrity issue is.
     */
    private void handleStaffDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {
        final Throwable realCause = dve.getMostSpecificCause();

        logger.error(dve.getMessage(), dve);
        throw new PlatformDataIntegrityException("error.msg.staff.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource: " + realCause.getMessage());
    }
}