package com.braveboy.hotelzagrous

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.LayoutDirection
import com.braveboy.hotelzagrous.app.shared.data.HotelRepository
import com.braveboy.hotelzagrous.app.shared.features.admin.AdminScreen
import com.braveboy.hotelzagrous.app.shared.features.admin.AdminViewModel
import com.braveboy.hotelzagrous.app.shared.features.reservation.ReservationScreen
import com.braveboy.hotelzagrous.app.shared.features.reservation.ReservationViewModel
import com.braveboy.hotelzagrous.designsystem.HotelZagrousTheme
import hotelzagrous.app.shared.generated.resources.Res
import hotelzagrous.app.shared.generated.resources.BHoma
import org.jetbrains.compose.resources.Font

@Composable
fun App(isAdmin: Boolean = false) {
    val repository = remember { HotelRepository() }
    val scope = rememberCoroutineScope()
    
    val farsiFontFamily = FontFamily(Font(Res.font.BHoma))

    val defaultTypography = Typography()
    val typography = Typography(
        displayLarge = defaultTypography.displayLarge.copy(fontFamily = farsiFontFamily),
        displayMedium = defaultTypography.displayMedium.copy(fontFamily = farsiFontFamily),
        displaySmall = defaultTypography.displaySmall.copy(fontFamily = farsiFontFamily),
        headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = farsiFontFamily),
        headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = farsiFontFamily),
        headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = farsiFontFamily),
        titleLarge = defaultTypography.titleLarge.copy(fontFamily = farsiFontFamily),
        titleMedium = defaultTypography.titleMedium.copy(fontFamily = farsiFontFamily),
        titleSmall = defaultTypography.titleSmall.copy(fontFamily = farsiFontFamily),
        bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = farsiFontFamily),
        bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = farsiFontFamily),
        bodySmall = defaultTypography.bodySmall.copy(fontFamily = farsiFontFamily),
        labelLarge = defaultTypography.labelLarge.copy(fontFamily = farsiFontFamily),
        labelMedium = defaultTypography.labelMedium.copy(fontFamily = farsiFontFamily),
        labelSmall = defaultTypography.labelSmall.copy(fontFamily = farsiFontFamily)
    )

    HotelZagrousTheme(typography = typography) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                if (isAdmin) {
                    val adminViewModel = remember { AdminViewModel(repository, scope) }
                    AdminScreen(adminViewModel)
                } else {
                    val resViewModel = remember { ReservationViewModel(repository, scope) }
                    ReservationScreen(resViewModel)
                }
            }
        }
    }
}
