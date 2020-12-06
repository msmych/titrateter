package dev.msmych.titrateter.bot

import ru.tinkoff.invest.openapi.models.Currency
import ru.tinkoff.invest.openapi.models.sandbox.CurrencyBalance
import ru.tinkoff.invest.openapi.models.sandbox.PositionBalance
import java.math.BigDecimal
import java.util.*

data class Config(val openApi: OpenApi) {

    data class OpenApi(
        val mode: Mode,
        val sandboxInitialCurrencyBalances: Collection<CurrencyBalance>,
        val sandboxInitialPositionBalances: Collection<PositionBalance>
    ) {

        enum class Mode {
            SANDBOX,
            PROD
        }

        companion object {
            fun load(props: Properties): OpenApi {
                return OpenApi(
                    Mode.valueOf(props.getProperty("openapi.mode")),
                    props.stringPropertyNames()
                        .filter { it.startsWith("openapi.balance.currency.") }
                        .map {
                            CurrencyBalance(
                                Currency.valueOf(it.substringAfter("openapi.balance.currency.")),
                                BigDecimal(props.getProperty(it))
                            )
                        },
                    props.stringPropertyNames()
                        .filter { it.startsWith("openapi.balance.position.") }
                        .map {
                            PositionBalance(
                                it.substringAfter("openapi.balance.position."),
                                BigDecimal(props.getProperty(it))
                            )
                        }
                )
            }
        }
    }

    companion object {
        fun load(propsFile: String): Config {
            val props = Properties()
            props.load(javaClass.classLoader.getResourceAsStream(propsFile))
            return Config(OpenApi.load(props))
        }
    }
}