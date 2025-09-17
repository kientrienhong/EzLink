package com.example.linkkeeper.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

val LocalCustomColors = staticCompositionLocalOf<CustomColors> {
    error("No CustomColors provided")
}

val LocalCustomTypography = staticCompositionLocalOf<CustomTypography> {
    error("No CustomTypography provided")
}

@Composable
fun LinkKeeperTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }

    val customColors = if (darkTheme) {
        CustomColors(
            primary = primaryCustomDark,
            primaryDark = primaryDarkCustomDark,
            primaryDarker = primaryDarkerCustomDark,
            primaryLight = primaryLightCustomDark,
            primaryDisabled = primaryDisabledCustomDark,
            background = backgroundCustomDark,
            surface = surfaceCustomDark,
            border = borderCustomDark,
            success = successCustomDark,
            successLight = successLightCustomDark,
            warning = warningCustomDark,
            warningLight = warningLightCustomDark,
            error = errorCustomDark,
            errorLight = errorLightCustomDark,
            text = textCustomDark,
            subtext = subtextCustomDark,
            textLink = textLinkCustomDark,
            textPlaceholder = textPlaceholderCustomDark,
            textError = textErrorCustomDark,
            textSuccess = textSuccessCustomDark,
            textWarning = textWarningCustomDark,
            textDisabled = textDisabledCustomDark
        )
    } else {
        CustomColors(
            primary = primaryCustomLight,
            primaryDark = primaryDarkCustomLight,
            primaryDarker = primaryDarkerCustomLight,
            primaryLight = primaryLightCustomLight,
            primaryDisabled = primaryDisabledCustomLight,
            background = backgroundCustomLight,
            surface = surfaceCustomLight,
            border = borderCustomLight,
            success = successCustomLight,
            successLight = successLightCustomLight,
            warning = warningCustomLight,
            warningLight = warningLightCustomLight,
            error = errorCustomLight,
            errorLight = errorLightCustomLight,
            text = textCustomLight,
            subtext = subtextCustomLight,
            textLink = textLinkCustomLight,
            textPlaceholder = textPlaceholderCustomLight,
            textError = textErrorCustomLight,
            textSuccess = textSuccessCustomLight,
            textWarning = textWarningCustomLight,
            textDisabled = textDisabledCustomLight
        )
    }

    val customTypography = CustomTypography(
        display = display,
        largeTitle = largeTitle,
        title = title,
        headline = headline,
        body = body,
        subhead = subhead,
        buttonText = buttonTextStyle,
        caption = caption,
        overline = overline
    )

    CompositionLocalProvider(
        LocalCustomColors provides customColors,
        LocalCustomTypography provides customTypography
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}

