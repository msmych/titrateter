package dev.msmych.titrateter.bot.processor

import com.pengrad.telegrambot.model.Update
import dev.msmych.telestella.bot.Bot
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ru.tinkoff.invest.openapi.MarketContext
import ru.tinkoff.invest.openapi.models.Currency.USD
import ru.tinkoff.invest.openapi.models.market.Candle
import ru.tinkoff.invest.openapi.models.market.CandleInterval.MONTH
import ru.tinkoff.invest.openapi.models.market.HistoricalCandles
import ru.tinkoff.invest.openapi.models.market.Instrument
import ru.tinkoff.invest.openapi.models.market.InstrumentType.Stock
import ru.tinkoff.invest.openapi.models.market.InstrumentsList
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*
import java.util.Optional.empty
import java.util.concurrent.CompletableFuture.completedFuture

internal class InstrumentByTickerProviderTest {

    private val marketContext = mockk<MarketContext>()

    private val provider = InstrumentByTickerProvider(marketContext)

    private val update = mockk<Update>()
    private val bot = mockk<Bot>()

    private val aapl = Instrument("BBG000B9XRY4", "AAPL", "US0378331005", BigDecimal("0.01"), 1, USD, "Apple", Stock)
    private val candle = Candle(
        "BBG000B9XRY4",
        MONTH,
        BigDecimal("120"),
        BigDecimal("126.66"),
        BigDecimal("129.56"),
        BigDecimal("119.8"),
        BigDecimal("398905944"),
        OffsetDateTime.now()
    )

    @Test
    fun `should return AAPL with price`() {
        every { update.message().text() } returns "AAPL"
        every { marketContext.searchMarketInstrumentsByTicker("AAPL") } returns completedFuture(
            InstrumentsList(1, listOf(aapl))
        )
        every { marketContext.getMarketCandles("BBG000B9XRY4", any(), any(), MONTH) } returns completedFuture(
            Optional.of(HistoricalCandles("BBG000B9XRY4", MONTH, listOf(candle)))
        )

        val text = provider.text(update, bot)

        assertThat(text).isEqualTo("""
            *Apple*
            `126.66$`
        """.trimIndent()
        )
    }

    @Test
    fun `should return not found for unknown ticker`() {
        every { update.message().text() } returns "B"
        every { marketContext.searchMarketInstrumentsByTicker("B") } returns completedFuture(
            InstrumentsList(0, listOf())
        )

        val text = provider.text(update, bot)

        assertThat(text).isEqualTo("Ticker B not found")
    }

    @Test
    fun `should return not found price for empty candles`() {
        every { update.message().text() } returns "AAPL"
        every { marketContext.searchMarketInstrumentsByTicker("AAPL") } returns completedFuture(
            InstrumentsList(1, listOf(aapl))
        )
        every { marketContext.getMarketCandles("BBG000B9XRY4", any(), any(), MONTH) } returns completedFuture(empty())

        val text = provider.text(update, bot)

        assertThat(text).isEqualTo("""
            *Apple*
            `N/A$`
        """.trimIndent())
    }
}