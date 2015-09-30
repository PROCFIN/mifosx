/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.forexexchange.api;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.codes.service.CodeValueReadPlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.forexexchange.data.ForexExchangeData;
import org.mifosplatform.organisation.forexexchange.service.ForexExchangePlatformService;
import org.mifosplatform.organisation.monetary.data.CurrencyData;
import org.mifosplatform.organisation.monetary.service.CurrencyReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.Collection;

@Path("/forexexchange")
@Component
@Scope("singleton")
public class ForexExchangeApiResource {

    private final String resourceNameForPermissions = "FOREXEXCHANGE";

    private final PlatformSecurityContext context;
    private final ForexExchangePlatformService readPlatformService;
    private final DefaultToApiJsonSerializer<ForexExchangeData> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final CurrencyReadPlatformService currencyReadPlatformService;
    private final CodeValueReadPlatformService codeValueReadPlatformService;

    @Autowired
    public ForexExchangeApiResource(final PlatformSecurityContext context, final ForexExchangePlatformService readPlatformService,
                                    final DefaultToApiJsonSerializer<ForexExchangeData> toApiJsonSerializer,
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

        final Collection<ForexExchangeData> exchangeRates = this.readPlatformService.retrieveAllForexExchanges(sqlSearch);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, exchangeRates, ForexExchangeApiConstants.FOREX_EXCHANGE_RESPONSE_DATA_PARAMETERS);
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String createExchangeRate(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createForexExchange().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @GET
    @Path("template")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String retrieveNewAccountDetails(@Context final UriInfo uriInfo, @QueryParam("type") final Integer type) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        ForexExchangeData forexExchangeData = ForexExchangeData.sensibleDefaultsForNewForexExchangeCreation();
        forexExchangeData = handleTemplate(forexExchangeData);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, forexExchangeData, ForexExchangeApiConstants.FOREX_EXCHANGE_RESPONSE_DATA_PARAMETERS);
    }

    private ForexExchangeData handleTemplate(final ForexExchangeData forexExchangeData) {
        final Collection<CurrencyData> allowedExchangeRateCurrencyOptions = this.currencyReadPlatformService.retrieveAllowedCurrencies();

        return new ForexExchangeData(forexExchangeData, allowedExchangeRateCurrencyOptions);
    }

    @GET
    @Path("{forexExchangeId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String retrieveExchangeRate(@PathParam("exchangeRateId") final Long forexExchangeId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        ForexExchangeData forexExchangeData = this.readPlatformService.retrieveForexExchange(forexExchangeId);
        if (settings.isTemplate()) {
            forexExchangeData = handleTemplate(forexExchangeData);
        }
        return this.toApiJsonSerializer.serialize(settings, forexExchangeData, ForexExchangeApiConstants.FOREX_EXCHANGE_RESPONSE_DATA_PARAMETERS);
    }

    @DELETE
    @Path("{forexExchangeId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String deleteExchangeRate(@PathParam("forexExchangeId") final Long forexExchangeId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteForexExchange(forexExchangeId).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @PUT
    @Path("{forexExchangeId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String updateExchangeRate(@PathParam("forexExchangeId") final Long forexExchangeId, final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateForexExchange(forexExchangeId).withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }
}