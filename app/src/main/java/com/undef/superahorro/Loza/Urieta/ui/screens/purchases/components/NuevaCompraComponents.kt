package com.undef.superahorro.Loza.Urieta.ui.screens.purchases.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.undef.superahorro.Loza.Urieta.R
import com.undef.superahorro.Loza.Urieta.ui.theme.*

@Composable
fun TicketCameraCard(
    hasTicket: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (hasTicket) TicketCapturedBg else MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(if (hasTicket) TicketCapturedIcon else MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (hasTicket) Icons.Filled.CheckCircle else Icons.Filled.AddAPhoto,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = if (hasTicket) stringResource(R.string.new_purchase_ticket_captured) else stringResource(R.string.new_purchase_attach_ticket),
                fontWeight = FontWeight.Bold,
                color = if (hasTicket) TicketCapturedText else MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = if (hasTicket) stringResource(R.string.new_purchase_change_ticket) else stringResource(R.string.new_purchase_attach_hint),
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun BankerStatusCard(safe: Boolean, message: String) {
    Card(
        modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (safe) BankerSafeBg else BankerUnsafeBg
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (safe) Icons.Filled.CheckCircle else Icons.Filled.AttachMoney,
                contentDescription = null,
                tint = if (safe) BankerSafe else BankerUnsafe
            )
            Spacer(Modifier.size(8.dp))
            Text(
                text = message,
                fontSize = 13.sp,
                color = if (safe) BankerSafeText else BankerUnsafeText,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
