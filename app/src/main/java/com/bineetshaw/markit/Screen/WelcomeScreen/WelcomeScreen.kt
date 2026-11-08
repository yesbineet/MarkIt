package com.bineetshaw.markit.Screen.WelcomeScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bineetshaw.markit.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms.fonts",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val InterFont = GoogleFont("Inter")

val InterFontFamily = FontFamily(
    Font(googleFont = InterFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = InterFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = InterFont, fontProvider = provider, weight = FontWeight.Bold)
)

@Composable
fun WelcomeScreen(
    onGetStartedClick: () -> Unit = {},
    onAlreadyHaveAccountClick: () -> Unit = onGetStartedClick
) {
    val gradientColors = listOf(
        Color(0xFF0A5CFF),
        Color(0xFF1E6DFF),
        Color(0xFF6897EB),
        Color(0xFFEAF2FF),
        Color(0xFFDCE9FF),
        Color(0xFFFFFFFF)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = linearGradient(colors = gradientColors)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Image(
                painter = painterResource(id = R.drawable.markittext),
                contentDescription = "MarkIt Logo",
                modifier = Modifier
                    .padding(top = 64.dp)
                    .size(width = 104.dp, height = 28.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Image(
                painter = painterResource(id = R.drawable.onboardimage),
                contentDescription = "Onboarding Image",
                modifier = Modifier.size(width = 300.dp, height = 350.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Attendance, sorted.\n" +
                        "Skip smart, stay safe.",
                color = Color(0xFF111111),
                fontSize = 29.sp,
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(100.dp))

            Box(
                modifier = Modifier
                    .size(width = 342.dp, height = 54.dp)
                    .background(Color(0xFF111111), shape = RoundedCornerShape(size = 27.dp))
                    .clickable { onGetStartedClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Get Started",
                    color = Color(0xFFFFFFFF),
                    fontSize = 15.sp,
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .size(width = 342.dp, height = 54.dp)
                    .background(Color(0xFFE8E8E8), shape = RoundedCornerShape(size = 27.dp))
                    .clickable { onAlreadyHaveAccountClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "I already have an account",
                    color = Color(0xFF111111),
                    fontSize = 15.sp,
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "By continuing you agree to our Terms of Service\n" +
                        "and Privacy Policy",
                color = Color(0xFF616161),
                fontSize = 12.sp,
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen()
}
