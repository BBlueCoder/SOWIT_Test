package com.bbluecoder.sowittest.ui.screens.mainScreenComponents

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
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
fun PolygonPointsErrorDialog(
    modifier: Modifier = Modifier,
    onConfirm : () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        icon = {
            Icon(Icons.Rounded.ErrorOutline, contentDescription = "Error Icon")
        },
        title = {
            Text(text = stringResource(R.string.can_t_save_polygon))
        },
        text = {
            Text(stringResource(R.string.cant_save_polygon_msg))
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("OK")
            }
        },
        modifier = modifier
    )
}