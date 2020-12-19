package dev.msmych.titrateter.bot.processor

import com.pengrad.telegrambot.model.Update
import dev.msmych.telestella.bot.Bot
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ru.tinkoff.invest.openapi.PortfolioContext
import ru.tinkoff.invest.openapi.models.portfolio.InstrumentType.Currency
import ru.tinkoff.invest.openapi.models.portfolio.Portfolio
import ru.tinkoff.invest.openapi.models.portfolio.Portfolio.PortfolioPosition
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.util.concurrent.CompletableFuture.completedFuture

internal class PortfolioProviderTest {

    private val portfolioContext = mockk<PortfolioContext>()

    private val provider = PortfolioProvider(portfolioContext)

    private val update = mockk<Update>()
    private val bot = mockk<Bot>()
    private val usdPosition = PortfolioPosition("BBG0013HGFT4", null, null, Currency, BigDecimal("1000"), ZERO, null, 1, null, null, "Доллар США")
    private val eurPosition = PortfolioPosition("BBG0013HJJ31", null, null, Currency, BigDecimal("200"), ZERO, null, 1, null, null, "Евро")

    @Test
    fun `should return portfolio details`() {
        every { portfolioContext.getPortfolio(null) } returns completedFuture(Portfolio(listOf(usdPosition, eurPosition)))

        val text = provider.text(update, bot)

        assertThat(text).isEqualTo("""
            *Portfolio*:
            `Доллар США`: `1000`
            `Евро`: `200`
        """.trimIndent())
    }
}