package com.bbluecoder.sowittest.ui.screens

import android.Manifest
import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bbluecoder.sowittest.findActivity
import com.bbluecoder.sowittest.hasPermission
import com.bbluecoder.sowittest.openAppSettings
import com.bbluecoder.sowittest.ui.screens.Constants.COARSE_LOCATION
import com.bbluecoder.sowittest.ui.screens.mainScreenComponents.LocationDialogPermission
import com.bbluecoder.sowittest.ui.screens.mainScreenComponents.LocationPermissionDescriptionDialog
import com.bbluecoder.sowittest.ui.screens.mainScreenComponents.Map
import com.bbluecoder.sowittest.ui.screens.mainScreenComponents.MapTopBar
import com.bbluecoder.sowittest.ui.screens.mainScreenComponents.PlotsList
import com.bbluecoder.sowittest.ui.screens.mainScreenComponents.PolygonDetailsDialog
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import java.util.Locale

object Constants {
    const val COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
    val colorsPallet = listOf(
        Color.Green,
        Color.Red,
        Color.Blue,
        Color.Yellow,
        Color.Magenta
    )
    val colorsNames = mapOf(
        Color.Green to "Green",
        Color.Red to "Red",
        Color.Blue to "Blue",
        Color.Yellow to "Yellow",
        Color.Magenta to "Magenta"
    )

}

@Composable
fun HandleLocationPermission(
    modifier: Modifier = Modifier,
    context: Context,
    updateLocation: () -> Unit
) {
    var openAlertDialog by remember {
        mutableStateOf(
            !hasPermission(COARSE_LOCATION, context)
        )
    }

    var showRationalDialog by remember {
        mutableStateOf(false)
    }


    val requestLocationPermissions = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            updateLocation()
        }
    }

    if (openAlertDialog) {
        LocationPermissionDescriptionDialog(onConfirm = {
            openAlertDialog = false
            val isPermanentlyDeclined = shouldShowRequestPermissionRationale(
                context.findActivity(),
                COARSE_LOCATION
            )

            if (isPermanentlyDeclined) {
                showRationalDialog = true
            } else {
                requestLocationPermissions.launch(COARSE_LOCATION)
            }
        })
    } else {
        updateLocation()
    }

    if (showRationalDialog) {
        LocationDialogPermission(
            onDismiss = { showRationalDialog = false },
            onConfirm = {
                context.findActivity().openAppSettings()
            }
        )
    }
}

@Composable
fun Home(modifier: Modifier = Modifier, viewModel: MainScreenViewModel) {
    val context = LocalContext.current

    viewModel.setLocationClient(LocationServices.getFusedLocationProviderClient(context))

    val cameraPosition by viewModel.cameraTarget.collectAsStateWithLifecycle()
    val isDrawing by viewModel.isDrawing.collectAsStateWithLifecycle()
    val showPolygonDetailsDialog by viewModel.showPolygonDetailsDialog.collectAsStateWithLifecycle()
    val points by viewModel.points.collectAsStateWithLifecycle()
    val isCameraAnimationEnabled by viewModel.isMapCameraAnimationEnabled.collectAsStateWithLifecycle()
    val polygonClr by viewModel.polygonColor.collectAsStateWithLifecycle()

    val polygonsUiState by viewModel.uiState.collectAsStateWithLifecycle()

    if(polygonsUiState is PolygonUiState.Success) {
        val polygons = (polygonsUiState as PolygonUiState.Success).polygons
        Log.d("Polygons","****************************** $polygons")
    }

    val geoCoder = Geocoder(context, Locale.getDefault())

    val cameraPositionState = rememberCameraPositionState()

    animateMapCameraPositon(cameraPositionState, cameraPosition,isCameraAnimationEnabled)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            MapTopBar(
                isEditing = isDrawing,
                onLocationClick = {
                    viewModel.fetchLocation()
                },
                onEditClick = {
                    viewModel.newPolygon()
                },
                onDoneClick = {
                    viewModel.drawPolygon()
                },
                onCancelClick = {
                    viewModel.cancelDrawing()
                }
            )
        },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {

            Map(
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    viewModel.handleMapOnClick(latLng,geoCoder)
                }
            ) {

                points.forEachIndexed { index, it ->
                    Circle(center = it, radius = 20.5, fillColor = Color.Green, strokeWidth = 0f)
                    if (index > 0) {
                        Polyline(
                            points = listOf(points[index - 1], it),
                            color = Color.Black,
                            width = 5f
                        )
                    }
                }

                if (!isDrawing && points.isNotEmpty()) {
                    Polygon(
                        points = points.toList(),
                        fillColor = polygonClr.copy(alpha = 0.5f),
                        strokeColor = Color.Black,
                        strokeWidth = 5f
                    )
                    if (showPolygonDetailsDialog) {
                        PolygonDetailsDialog(
                            city = viewModel.polygonLocationCity,
                            color = polygonClr,
                            onDismiss = {
                                viewModel.cancelDrawing()
                            },
                            onSave = { name,city, color ->
                                viewModel.savePolygon(name,city, color)
                            }
                        )
                    }
                }
            }

            HandleLocationPermission(context = context, updateLocation = {
                viewModel.fetchLocation()
            })

            PlotsList(plotsState = polygonsUiState, onItemClick = {
                viewModel.handlePlotItemClick(it)
            })

        }
    }
}

@Composable
private fun animateMapCameraPositon(
    cameraPositionState: CameraPositionState,
    cameraPosition: LatLng,
    isCameraAnimationEnabled: Boolean
) {
    if(!isCameraAnimationEnabled)
        return

    LaunchedEffect(cameraPosition) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newCameraPosition(
                CameraPosition.fromLatLngZoom(
                    LatLng(cameraPosition.latitude, cameraPosition.longitude),
                    15f
                )
            ),
            durationMs = 800
        )
    }
}



