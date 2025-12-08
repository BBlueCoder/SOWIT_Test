package com.bbluecoder.sowittest.ui.screens.mainScreenComponents

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.bbluecoder.sowittest.R

@Composable
fun LocationPermissionDescriptionDialog(onConfirm : () -> Unit,modifier: Modifier = Modifier) {
    AlertDialog(
        onDismissRequest = {

        },
        icon = {
            Icon(Icons.Rounded.Info, contentDescription = stringResource(R.string.info_about_location_permission))
        },
        title = {
            Text(text = stringResource(R.string.location_permission_required))
        },
        text = {
            Text(stringResource(R.string.info_location_permission_description))
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("OK")
            }
        },
        modifier = modifier
    )
}