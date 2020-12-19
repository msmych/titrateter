package dev.msmych.titrateter.bot.processor

import com.pengrad.telegrambot.model.Update
import dev.msmych.telestella.bot.Bot
import dev.msmych.telestella.bot.update.processor.TextProvider
import dev.msmych.titrateter.openapi.symbol
import ru.tinkoff.invest.openapi.MarketContext
import ru.tinkoff.invest.openapi.models.market.CandleInterval.MONTH
import java.math.BigDecimal
import java.time.OffsetDateTime.now

class InstrumentByTickerProvider(private val marketContext: MarketContext) : TextProvider {

    override fun text(update: Update, bot: Bot): String {
        val ticker = update.message().text()
        return marketContext.searchMarketInstrumentsByTicker(ticker)
            .thenApply { it.instruments.firstOrNull()  }
            .thenApply {
                if (it != null) {
                    "*${it.name}*\n`${lastPrice(it.figi) ?: "N/A"}${it.currency?.symbol()}`"
                } else {
                    "Ticker $ticker not found"
                }
            }
            .join()
    }

    private fun lastPrice(figi: String): BigDecimal? {
        return marketContext.getMarketCandles(figi, now().minusMonths(1), now(), MONTH)
            .thenApply { hc ->
                hc
                    .map { it.candles.last().closePrice }
                    .orElse(null)
            }
            .join()
    }
}