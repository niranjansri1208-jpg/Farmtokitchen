package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BatchStatus
import com.example.data.model.VegetableType
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.DeepGreen
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.FreshGreen
import com.example.ui.theme.LimeGreen
import com.example.ui.theme.StatusDelivered
import com.example.ui.theme.StatusMarket
import com.example.ui.theme.StatusTransit

@Composable
fun VegetableBadge(
    vegetableName: String,
    modifier: Modifier = Modifier
) {
    val veg = VegetableType.fromString(vegetableName)
    val color = Color(veg.iconColorHex)

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.14f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = veg.emoji,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = vegetableName,
                color = ForestGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun BatchStatusChip(
    status: BatchStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, icon) = when (status) {
        BatchStatus.REGISTERED -> Triple(Color(0xFFE8F5E9), FreshGreen, Icons.Default.Place)
        BatchStatus.AT_MARKET -> Triple(Color(0xFFE3F2FD), StatusMarket, Icons.Default.Store)
        BatchStatus.IN_TRANSIT -> Triple(Color(0xFFFFF3E0), StatusTransit, Icons.Default.LocalShipping)
        BatchStatus.AT_RETAIL_SHOP -> Triple(Color(0xFFEDE7F6), Color(0xFF7E57C2), Icons.Default.ShoppingCart)
        BatchStatus.DELIVERED -> Triple(Color(0xFFE0F2F1), StatusDelivered, Icons.Default.CheckCircle)
        BatchStatus.SOLD_OUT -> Triple(Color(0xFFFEE2E2), Color(0xFFDC2626), Icons.Default.ShoppingCart)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = status.displayName,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun OrganicCertificationBadge(
    isOrganic: Boolean,
    modifier: Modifier = Modifier
) {
    if (isOrganic) {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(8.dp),
            color = FreshGreen.copy(alpha = 0.12f),
            border = androidx.compose.foundation.BorderStroke(1.dp, FreshGreen.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(FreshGreen)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "100% ORGANIC",
                    color = DeepGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
