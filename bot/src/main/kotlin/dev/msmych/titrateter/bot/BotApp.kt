package dev.msmych.titrateter.bot

import dev.msmych.telestella.bot.Bot
import dev.msmych.telestella.bot.update.dispatcher.SingleProcessorUpdateDispatcher
import dev.msmych.telestella.bot.update.listener.OneTryUpdatesListener
import dev.msmych.telestella.bot.update.predicate.CommandPredicate.Companion.command
import dev.msmych.telestella.bot.update.predicate.TextMessagePredicate.Companion.regex
import dev.msmych.telestella.bot.update.processor.AnswerMessageProcessor.Companion.answer
import dev.msmych.titrateter.bot.processor.InstrumentByTickerProvider
import dev.msmych.titrateter.bot.processor.PortfolioProvider
import dev.msmych.titrateter.openapi.openApi

fun main(args: Array<String>) {
    val config = Config.load("app.properties")

    val openApi = openApi(args[0], config.openApi)

    val bot = Bot(args[1])

    val portfolioProvider = PortfolioProvider(openApi.portfolioContext)
    val instrumentByTickerProvider = InstrumentByTickerProvider(openApi.marketContext)

    val dispatcher = SingleProcessorUpdateDispatcher(
        command("portfolio") to answer(portfolioProvider),
        regex("[A-Z]{1,5}") to answer(instrumentByTickerProvider)
    )

    bot.setUpdatesListener(OneTryUpdatesListener(bot, dispatcher))
    println("Поехали")
}