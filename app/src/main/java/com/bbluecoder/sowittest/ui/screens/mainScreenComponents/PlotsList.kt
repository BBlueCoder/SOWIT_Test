package com.bbluecoder.sowittest.ui.screens.mainScreenComponents

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bbluecoder.sowittest.db.MPolygon
import com.bbluecoder.sowittest.ui.screens.PolygonUiState

@Composable
fun PlotsList(
    modifier: Modifier = Modifier,
    plotsState: PolygonUiState,
    expanded: Boolean,
    isLandscape: Boolean,
    onArrowClick : () -> Unit,
    onItemClick: (MPolygon) -> Unit
) {
    if (isLandscape) {
        Column(
            modifier = modifier
                .width(150.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("All Plots")

            PlotsLazyColumn(
                modifier = Modifier.fillMaxWidth(),
                plotsState = plotsState,
                onItemClick = onItemClick
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("All Plots")

                IconButton(onClick = onArrowClick) {
                    Icon(
                        imageVector = if (expanded) Icons.Rounded.KeyboardArrowUp
                        else Icons.Rounded.KeyboardArrowDown,
                        contentDescription = "Arrow Icon Button"
                    )
                }
            }

            if (expanded) {
                PlotsLazyColumn(
                    plotsState = plotsState,
                    onItemClick = onItemClick
                )
            }
        }

    }
}

@Composable
fun PlotsLazyColumn(
    modifier: Modifier = Modifier,
    plotsState: PolygonUiState,
    onItemClick: (MPolygon) -> Unit
) {
    if (plotsState is PolygonUiState.Success) {
        val plots = plotsState.polygons
        LazyColumn(modifier = modifier) {
            items(items = plots, key = { it.polygon.id }) {
                PlotItem(polygon = it, onItemClick = onItemClick)
            }
        }
    }
}

@Composable
private fun PlotItem(
    modifier: Modifier = Modifier,
    polygon: MPolygon,
    onItemClick: (MPolygon) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clickable {
                onItemClick(polygon)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Log.d("PlotItem", "clr from db : ${polygon.polygon.color}")
        Icon(
            Icons.Filled.Circle,
            "Leading Icon of Color",
            tint = polygon.polygon.color
        )
        Spacer(modifier = Modifier.padding(horizontal = 5.dp))
        Column {
            Text(
                text = polygon.polygon.name,
                fontSize = 12.sp,
                fontWeight = MaterialTheme.typography.titleSmall.fontWeight
            )
            Text(
                text = polygon.polygon.city,
                fontSize = 8.sp,
                fontWeight = MaterialTheme.typography.titleSmall.fontWeight,
                color = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}