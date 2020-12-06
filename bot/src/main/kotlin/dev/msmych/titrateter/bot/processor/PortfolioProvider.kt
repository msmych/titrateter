package dev.msmych.titrateter.bot.processor

import com.pengrad.telegrambot.model.Update
import dev.msmych.telestella.bot.Bot
import dev.msmych.telestella.bot.update.processor.TextProvider
import ru.tinkoff.invest.openapi.PortfolioContext

class PortfolioProvider(private val portfolioContext: PortfolioContext) : TextProvider {

    override fun text(update: Update, bot: Bot): String {
        return portfolioContext.getPortfolio(null)
            .thenApply {
                it.positions.joinToString(prefix = "*Portfolio*:\n", separator = "\n") { pos ->
                    "`${pos.name}`: `${pos.balance.toPlainString()}`"
                }
            }
            .join()
    }
}