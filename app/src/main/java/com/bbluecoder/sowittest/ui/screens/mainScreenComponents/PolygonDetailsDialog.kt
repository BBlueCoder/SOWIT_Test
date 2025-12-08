package com.bbluecoder.sowittest.ui.screens.mainScreenComponents

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.bbluecoder.sowittest.ui.screens.Constants
import kotlin.math.exp

@Composable
fun PolygonDetailsDialog(
    modifier: Modifier = Modifier,
    city: String,
    color: Color,
    onDismiss: () -> Unit,
    onSave: (String, String, Color) -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("New Plot")
                var plotName by remember { mutableStateOf("") }

                TextField(
                    value = plotName,
                    onValueChange = { plotName = it },
                    placeholder = {
                        Text(
                            text = "Enter a Name",
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                var city by remember { mutableStateOf(city) }
                val isSaveEnabled = plotName.isNotEmpty()

                TextField(
                    value = city,
                    onValueChange = { city = it },
                    placeholder = {
                        Text(
                            text = "Enter City",
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                var selectedColorState by remember {
                    mutableStateOf(color)
                }

                ColorsDropDownMenu(
                    selectedColorState = selectedColorState,
                    onColorSelected = {
                        selectedColorState = it
                    }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    TextButton(
                        onClick = { onSave(plotName,city, selectedColorState) },
                        enabled = isSaveEnabled
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }

}

@Composable
fun ColorsDropDownMenu(
    modifier: Modifier = Modifier,
    selectedColorState: Color,
    onColorSelected: (Color) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .clickable {
                    expanded = true
                }
        ) {
            Icon(
                Icons.Filled.Circle,
                "Leading Icon of Color",
                tint = selectedColorState
            )
            Text(Constants.colorsNames[selectedColorState] ?: "")
            Icon(
                imageVector = if (expanded) Icons.Rounded.KeyboardArrowUp
                else Icons.Rounded.KeyboardArrowDown,
                contentDescription = "Arrow"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Constants.colorsPallet.forEach {
                DropdownMenuItem(
                    text = { Text(Constants.colorsNames[it] ?: "") },
                    onClick = {
                        onColorSelected(it)
                        expanded = false
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Circle,
                            "Leading Icon of Color",
                            tint = it
                        )
                    }
                )
            }
        }
    }

}