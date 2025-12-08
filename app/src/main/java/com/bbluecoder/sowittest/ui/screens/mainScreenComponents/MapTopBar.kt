package com.bbluecoder.sowittest.ui.screens.mainScreenComponents

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

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