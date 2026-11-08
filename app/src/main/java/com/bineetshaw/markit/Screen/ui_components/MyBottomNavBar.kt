package com.bineetshaw.markit.Screen.ui_components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bineetshaw.markit.R

@Preview(showBackground = true, backgroundColor = 0xFF111111)
@Composable
fun MyBottomNavBar(
    modifier: Modifier = Modifier,
    selectedIndex: Int = 0,
    onItemSelected: ((Int) -> Unit)? = null
) {
    var internalSelectedIndex by remember { mutableIntStateOf(selectedIndex) }
    val currentIndex = if (onItemSelected != null) selectedIndex else internalSelectedIndex

    val navItems = listOf(
        NavItem(title = "Home", icon = R.drawable.home),
        NavItem(title = "To Do", icon = R.drawable.todo),
        NavItem(title = "Stats", icon = R.drawable.chartup),
        NavItem(title = "Profile", icon = R.drawable.profile)
    )

    Row(
        modifier = modifier
            .navigationBarsPadding()
            .fillMaxWidth()
            .padding(horizontal = 45.dp, vertical = 24.dp) // Adjusted padding for balanced side spacing
            .height(72.dp) // Slightly increased height to accommodate the internal capsule comfortably
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(36.dp)
            )
            .background(Color.White, shape = RoundedCornerShape(36.dp))
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        navItems.forEachIndexed { index, item ->
            val isSelected = currentIndex == index

            Column(
                modifier = Modifier
                    .weight(1f)
                    // 1. Apply the selection capsule color and shape over the whole column block
                    .background(
                        color = if (isSelected) Color(0xFFF4F5F7) else Color.Transparent,
                        shape = RoundedCornerShape(36.dp)
                    )
                    // 2. Clip to prevent the click ripple from bleeding outside the corners
                    .clip(RoundedCornerShape(36.dp))
                    .clickable {
                        if (onItemSelected != null) {
                            onItemSelected(index)
                        } else {
                            internalSelectedIndex = index
                        }
                    }
                    .padding(vertical = 5.dp), // Vertical padding inside the capsule boundaries
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = item.icon),
                    contentDescription = item.title,
                    tint = if (isSelected) Color(0xFF111111) else Color(0xFF616161),
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = item.title,
                    color = if (isSelected) Color(0xFF111111) else Color(0xFF616161),
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

data class NavItem(
    val title: String,
    val icon: Int
)
