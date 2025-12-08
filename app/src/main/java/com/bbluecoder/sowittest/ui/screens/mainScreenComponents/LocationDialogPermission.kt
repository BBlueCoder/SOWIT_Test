package com.bbluecoder.sowittest.ui.screens.mainScreenComponents

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.bbluecoder.sowittest.R

@Composable
fun LocationDialogPermission(
    modifier: Modifier = Modifier,
    onDismiss : () -> Unit,
    onConfirm : () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
        onDismiss()
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        icon = {
            Icon(Icons.Rounded.LocationOn, contentDescription = stringResource(R.string.info_about_location_permission))
        },
        title = {
            Text(text = "Location Permission")
        },
        text = {
            Text("To allow the app to access device's location, enable location in the settings")
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Go To Settings")
            }
        },
        modifier = modifier
    )
}