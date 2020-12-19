package dev.msmych.titrateter.openapi

import ru.tinkoff.invest.openapi.models.Currency
import ru.tinkoff.invest.openapi.models.Currency.*

fun Currency.symbol(): String = when (this) {
    RUB -> "₽"
    USD -> "$"
    EUR -> "€"
}