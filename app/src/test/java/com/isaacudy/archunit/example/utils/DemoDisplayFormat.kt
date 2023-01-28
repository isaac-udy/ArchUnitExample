package com.isaacudy.archunit.example.utils

import com.tngtech.archunit.ArchConfiguration
import com.tngtech.archunit.base.HasDescription
import com.tngtech.archunit.lang.FailureDisplayFormat
import com.tngtech.archunit.lang.FailureMessages
import com.tngtech.archunit.lang.Priority
import java.util.stream.Collectors

class DemoDisplayFormat : FailureDisplayFormat {
    override fun formatFailure(rule: HasDescription, failureMessages: FailureMessages, priority: Priority): String {
        val failureDetails: String = failureMessages.stream()
            .map { message: String ->
                packageNames
                    .sortedByDescending { it.count { c -> c == '.' } }
                    .fold(message) { result, pkg ->
                        result.replace(
                            Regex(Regex.escapeReplacement("$pkg."))
                        ) { matchResult ->
                            when {
                                result.getOrNull(matchResult.range.first - 1) == '\'' -> {
                                    "$pkg."
                                }
                                else -> {
                                    ""
                                }
                            }
                        }
                    }
            }
            .collect(Collectors.joining(System.lineSeparator() + System.lineSeparator()))

        val fullMessage = String.format(
            "Architecture Rule was violated (%s)\nRule: %s\n%n%s",
            failureMessages.informationAboutNumberOfViolations, rule.description, failureDetails
        )

        val shortMessage = String.format(
            "Architecture Rule was violated (%s), see logs for more information",
            failureMessages.informationAboutNumberOfViolations
        )

        System.err.println(fullMessage)
        return shortMessage
    }

    companion object {
        private val packageNames by lazy {
            Package.getPackages().map { it.name }
        }

        fun install() {
            ArchConfiguration.get().setProperty("failureDisplayFormat", DemoDisplayFormat::class.java.name)
        }
    }
}