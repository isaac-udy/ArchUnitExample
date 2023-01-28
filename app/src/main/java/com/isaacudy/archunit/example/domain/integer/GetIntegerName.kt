package com.isaacudy.archunit.example.domain.integer

import com.ibm.icu.text.RuleBasedNumberFormat
import com.isaacudy.archunit.example.domain.integer.model.IntegerModel
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetIntegerName @Inject constructor() {
    operator fun invoke(integer: IntegerModel): String {
        return integer.value.intValueExact().convertToWord()
    }
}

private fun Int.convertToWord(): String {
    val ruleBasedNumberFormat = RuleBasedNumberFormat(Locale.US, RuleBasedNumberFormat.SPELLOUT)
    return ruleBasedNumberFormat.format(this).replaceFirstChar { it.uppercase() }
}