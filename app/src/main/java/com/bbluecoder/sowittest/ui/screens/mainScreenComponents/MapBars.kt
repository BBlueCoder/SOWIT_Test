package com.bbluecoder.sowittest.ui.screens.mainScreenComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapTopBar(
    modifier: Modifier = Modifier,
    isEditing : Boolean = false,
    onLocationClick : () -> Unit,
    onEditClick : () -> Unit,
    onDoneClick : () -> Unit,
    onCancelClick : () -> Unit
) {
    TopAppBar(
        title = { Text("SOWIT Test") },
        actions = {
            if(isEditing) {
                Editing(onDoneClick = onDoneClick, onCancelClick = onCancelClick)
            }else {
                NonEditing(onLocationClick = onLocationClick, onEditClick = onEditClick)
            }
        }
    )
}

@Composable
fun MapBottomBar(
    modifier: Modifier = Modifier,
    isEditing : Boolean = false,
    onLocationClick : () -> Unit,
    onEditClick : () -> Unit,
    onDoneClick : () -> Unit,
    onCancelClick : () -> Unit
) {
    ElevatedCard(
        modifier = modifier
            .width(180.dp)
            .height(40.dp),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if(isEditing) {
                Editing(onDoneClick = onDoneClick, onCancelClick = onCancelClick)
            }else {
                NonEditing(onLocationClick = onLocationClick, onEditClick = onEditClick)
            }
        }
    }
}

@Composable
private fun NonEditing(modifier: Modifier = Modifier,onLocationClick : () -> Unit,onEditClick : () -> Unit,) {
    IconButton(onClick = onLocationClick) {
        Icon(Icons.Rounded.MyLocation, contentDescription = "My Location Icon")
    }

    IconButton(onClick = onEditClick) {
        Icon(
            Icons.Rounded.Edit,
            "Edit Icon Button"
        )
    }
}

@Composable
private fun Editing(modifier: Modifier = Modifier,onDoneClick: () -> Unit, onCancelClick : () -> Unit) {
    TextButton(onClick = onCancelClick) {
        Text("Cancel")
    }

    TextButton(onClick = onDoneClick) {
        Text("Done")
    }
}