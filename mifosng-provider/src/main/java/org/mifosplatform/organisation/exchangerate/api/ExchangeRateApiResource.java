/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.exchangerate.api;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.infrastructure.codes.service.CodeValueReadPlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.exchangerate.data.ExchangeRateData;
import org.mifosplatform.organisation.exchangerate.service.ExchangeRatePlatformService;
import org.mifosplatform.organisation.monetary.data.CurrencyData;
import org.mifosplatform.organisation.monetary.service.CurrencyReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Path("/exchangerate")
@Component
@Scope("singleton")
public class ExchangeRateApiResource {

    /**
     * The set of parameters that are supported in response for
     * {@link org.mifosplatform.organisation.exchangerate.data.ExchangeRateData}.
     */
    private final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList("id", "firstname", "lastname", "displayName",
            "officeId", "officeName", "isLoanOfficer", "externalId", "mobileNo", "allowedOffices", "isActive", "joiningDate"));

    private final String resourceNameForPermissions = "EXCHANGERATE";

    private final PlatformSecurityContext context;
    private final ExchangeRatePlatformService readPlatformService;
    private final DefaultToApiJsonSerializer<ExchangeRateData> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final CurrencyReadPlatformService currencyReadPlatformService;
    private final CodeValueReadPlatformService codeValueReadPlatformService;

    @Autowired
    public ExchangeRateApiResource(final PlatformSecurityContext context, final ExchangeRatePlatformService readPlatformService,
                                   final DefaultToApiJsonSerializer<ExchangeRateData> toApiJsonSerializer,
                                   final ApiRequestParameterHelper apiRequestParameterHelper,
                                   final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
                                   final CurrencyReadPlatformService currencyReadPlatformService,
                                   final CodeValueReadPlatformService codeValueReadPlatformService) {
        this.context = context;
        this.readPlatformService = readPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.currencyReadPlatformService = currencyReadPlatformService;
        this.codeValueReadPlatformService = codeValueReadPlatformService;
    }

    @GET
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String retrieveExchangeRate(@Context final UriInfo uriInfo, @QueryParam("sqlSearch") final String sqlSearch) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final Collection<ExchangeRateData> exchangeRates = this.readPlatformService.retrieveAllExchangeRates(sqlSearch);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, exchangeRates, ExchangeRateApiConstants.EXCHANGE_RATE_RESPONSE_DATA_PARAMETERS);
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String createExchangeRate(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createExchangeRate().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @GET
    @Path("template")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String retrieveNewAccountDetails(@Context final UriInfo uriInfo, @QueryParam("type") final Integer type) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        ExchangeRateData exchangeRateData = ExchangeRateData.sensibleDefaultsForNewExchangeRateCreation();
        exchangeRateData = handleTemplate(exchangeRateData);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, exchangeRateData, RESPONSE_DATA_PARAMETERS);
    }

    private ExchangeRateData handleTemplate(final ExchangeRateData exchangeRateData) {
        final Collection<CodeValueData> allowedExchangeRateTypeOptions = this.codeValueReadPlatformService
                .retrieveCodeValuesByCode(ExchangeRateApiConstants.TYPE_OPTION_CODE_NAME);
        final Collection<CurrencyData> allowedExchangeRateCurrencyOptions = this.currencyReadPlatformService.retrieveAllowedCurrenciesExceptHomeCurrency();

        return new ExchangeRateData(exchangeRateData, allowedExchangeRateCurrencyOptions, allowedExchangeRateTypeOptions);
    }

    @GET
    @Path("{exchangeRateId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String retrieveExchangeRate(@PathParam("exchangeRateId") final Long exchangeRateId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        ExchangeRateData exchangeRateData = this.readPlatformService.retrieveExchangeRate(exchangeRateId);
        if (settings.isTemplate()) {
            exchangeRateData = handleTemplate(exchangeRateData);
        }
        return this.toApiJsonSerializer.serialize(settings, exchangeRateData, ExchangeRateApiConstants.EXCHANGE_RATE_RESPONSE_DATA_PARAMETERS);
    }

    @PUT
    @Path("{exchangeRateId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String updateExchangeRate(@PathParam("exchangeRateId") final Long exchangeRateId, final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateExchangeRate(exchangeRateId).withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }
}