package com.bbluecoder.sowittest.ui.screens.mainScreenComponents

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.GoogleMapComposable

@Composable
fun Map(
    modifier: Modifier = Modifier,
    cameraPositionState: CameraPositionState,
    onMapClick : (LatLng) -> Unit,
    polygon : @Composable @GoogleMapComposable () -> Unit = {}
    ) {
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        onMapClick = onMapClick,
        cameraPositionState=cameraPositionState,
    ) {
        polygon()
    }
}