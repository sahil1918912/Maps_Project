package com.ahmedabad.mapsproject.presentation.ui.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ahmedabad.mapsproject.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CentralAppTopBar(
    title: String,
    showBackIcon: Boolean = true,
    showFilterIcon: Boolean = false,
    onBackClick: () -> Unit = {},
    onLogoClick: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    sortIcon: @Composable (() -> Unit)? = null,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        navigationIcon = {
            Row {
                if (showBackIcon) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    IconButton(onClick = onLogoClick) {
                        Image(
                            modifier = Modifier.size(35.dp),
                            painter = painterResource(id = R.drawable.maps_logo),
                            contentDescription = "Logo",
                        )
                    }
                }
            }
        },
        actions = {
            if (showFilterIcon) {
                if (sortIcon != null) {
                    IconButton(onClick = onFilterClick) {
                        sortIcon()
                    }
                } else {
                    IconButton(onClick = onFilterClick) {
                        Icon(
                            modifier = Modifier.size(25.dp),
                            painter = painterResource(id = R.drawable.filter_icon),
                            contentDescription = "Filter",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        },

        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    )
}