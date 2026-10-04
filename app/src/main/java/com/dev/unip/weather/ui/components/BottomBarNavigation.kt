package com.dev.unip.weather.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dev.unip.weather.R
import com.dev.unip.weather.ui.theme.WeatherAccentCyan
import com.dev.unip.weather.ui.theme.WeatherSkyBlue

@Composable
fun BottomNavigator(
    currentRoute: String = "home",
    onSelectedDestination: (String) -> Unit = {}
) {
    val activeGradient = Brush.horizontalGradient(
        listOf(
            WeatherSkyBlue,
            WeatherAccentCyan
        )
    )

    val items = listOf(
        NavPillItem(
            route = "home",
            iconRes = R.drawable.world
        ),
        NavPillItem(
            route = "weather",
            iconRes = R.drawable.catalog
        )
    )

    Card(
        modifier = Modifier
            .height(68.dp)
            .fillMaxWidth(0.88f)
            .border(
                shape = RoundedCornerShape(100.dp),
                color = Color.White.copy(alpha = 0.25f),
                width = 1.dp
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xDD0D1838)
        ),
        shape = RoundedCornerShape(100.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(100.dp))
                        .background(
                            brush = if (isSelected) activeGradient else Brush.linearGradient(
                                listOf(Color.Transparent, Color.Transparent)
                            )
                        )
                        .clickable { onSelectedDestination(item.route) },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    ) {
                        Icon(
                            painter = painterResource(item.iconRes),
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

data class NavPillItem(
    val route: String,
    val iconRes: Int
)

@Preview
@Composable
fun ShowBottomNavPreview() {
    BottomNavigator()
}
