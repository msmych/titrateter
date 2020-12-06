package dev.msmych.titrateter.openapi

import dev.msmych.titrateter.bot.Config
import dev.msmych.titrateter.bot.Config.OpenApi.Mode.PROD
import dev.msmych.titrateter.bot.Config.OpenApi.Mode.SANDBOX
import ru.tinkoff.invest.openapi.OpenApi
import ru.tinkoff.invest.openapi.okhttp.OkHttpOpenApiFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.logging.Logger

fun openApi(token: String, config: Config.OpenApi): OpenApi {
    val factory = OkHttpOpenApiFactory(token, Logger.getAnonymousLogger())
    val executor = Executors.newSingleThreadExecutor()
    return when (config.mode) {
        SANDBOX -> {
            val api = factory.createSandboxOpenApiClient(executor)
            api.sandboxContext.performRegistration(null).join()
            CompletableFuture.allOf(
                *config.sandboxInitialCurrencyBalances
                    .map { api.sandboxContext.setCurrencyBalance(it, null) }
                    .toTypedArray(),
                *config.sandboxInitialPositionBalances
                    .map { api.sandboxContext.setPositionBalance(it, null) }
                    .toTypedArray())
                .join()
            api
        }
        PROD -> factory.createOpenApiClient(executor)
    }
}