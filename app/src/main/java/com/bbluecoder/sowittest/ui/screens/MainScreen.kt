package com.bbluecoder.sowittest.ui.screens

import android.Manifest
import android.content.Context
import android.content.res.Configuration
import android.location.Geocoder
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
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
import com.bbluecoder.sowittest.ui.screens.mainScreenComponents.MapBottomBar
import com.bbluecoder.sowittest.ui.screens.mainScreenComponents.MapTopBar
import com.bbluecoder.sowittest.ui.screens.mainScreenComponents.PlotsList
import com.bbluecoder.sowittest.ui.screens.mainScreenComponents.PolygonDetailsDialog
import com.bbluecoder.sowittest.ui.screens.mainScreenComponents.PolygonPointsErrorDialog
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
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

    if(hasPermission(COARSE_LOCATION,context)) {
        viewModel.setLocationClient(LocationServices.getFusedLocationProviderClient(context))
    }

    val cameraPosition by viewModel.cameraTarget.collectAsStateWithLifecycle()
    val isDrawing by viewModel.isDrawing.collectAsStateWithLifecycle()
    val showPolygonDetailsDialog by viewModel.showPolygonDetailsDialog.collectAsStateWithLifecycle()
    val points by viewModel.points.collectAsStateWithLifecycle()
    val isCameraAnimationEnabled by viewModel.isMapCameraAnimationEnabled.collectAsStateWithLifecycle()
    val polygonClr by viewModel.polygonColor.collectAsStateWithLifecycle()
    val showPolygonPointsError by viewModel.showPolygonPointsError.collectAsStateWithLifecycle()

    val bounds by viewModel.bounds.collectAsStateWithLifecycle()

    val polygonsUiState by viewModel.uiState.collectAsStateWithLifecycle()

    var plotsListExpanded by remember {
        mutableStateOf(false)
    }

    val geoCoder = Geocoder(context, Locale.getDefault())

    val cameraPositionState = rememberCameraPositionState()

    var isMapLoaded by remember {
        mutableStateOf(false)
    }

    var boundsDistance by remember {
        mutableStateOf(Pair(0f, 0f))
    }


    animateMapCameraPositon(
        cameraPositionState,
        cameraPosition,
        isCameraAnimationEnabled,
        bounds,
        viewModel.isPolygonAnimating,
    )

    HandleCameraChanges(
        cameraPositionState = cameraPositionState,
        isMapLoaded = isMapLoaded,
    ) {
        boundsDistance = it
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (!isLandscape) {
                MapTopBar(
                    isEditing = isDrawing,
                    onLocationClick = {
                        viewModel.fetchLocation()
                    },
                    onEditClick = {
                        viewModel.newPolygon()
                        plotsListExpanded = false
                    },
                    onDoneClick = {
                        viewModel.drawPolygon()
                    },
                    onCancelClick = {
                        viewModel.cancelDrawing()
                    }
                )
            }
        },
    ) { innerPadding ->
        AdaptiveLayout(
            innerPadding = innerPadding,
            isLandscape = isLandscape
        ) { plotsListModifier, bottomBarModifier ->

            if (showPolygonPointsError) {
                PolygonPointsErrorDialog(onConfirm = {
                    viewModel.hidePolygonPointsError()
                })
            }

            Map(
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    viewModel.handleMapOnClick(latLng, geoCoder)
                },
                onMapLoaded = {
                    isMapLoaded = true
                }
            ) {

                points.forEachIndexed { index, it ->
                    Circle(
                        center = it,
                        radius = calculateDynamicRadius(boundsDistance.first),
                        fillColor = Color.Green,
                        strokeWidth = 0f
                    )
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
                            onSave = { name, city, color ->
                                viewModel.savePolygon(name, city, color)
                            }
                        )
                    }
                }
            }

            HandleLocationPermission(context = context, updateLocation = {
                viewModel.fetchLocation()
            })

            PlotsList(
                modifier = plotsListModifier,
                plotsState = polygonsUiState,
                isLandscape = isLandscape,
                expanded = plotsListExpanded,
                onArrowClick = { plotsListExpanded = !plotsListExpanded },
                onItemClick = {
                    viewModel.handlePlotItemClick(it)
                    plotsListExpanded = false
                }
            )

            if (isLandscape) {
                MapBottomBar(
                    modifier = bottomBarModifier,
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
            }
        }
    }
}

@Composable
fun AdaptiveLayout(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    isLandscape: Boolean,
    content: @Composable BoxScope.(plotsList: Modifier, bottomBar: Modifier) -> Unit
) {

    Box(modifier = modifier.padding(innerPadding)) {
        content(
            if (isLandscape) Modifier.align(Alignment.TopStart) else Modifier,
            Modifier.align(Alignment.BottomCenter)
        )
    }

}

@Composable
private fun animateMapCameraPositon(
    cameraPositionState: CameraPositionState,
    cameraPosition: LatLng,
    isCameraAnimationEnabled: Boolean,
    bounds: LatLngBounds? = null,
    isPolygonAnimating: Boolean = false,
    zoom: Float = 15f
) {
    if (!isCameraAnimationEnabled)
        return

    LaunchedEffect(cameraPosition,bounds) {
        if (isPolygonAnimating) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngBounds(bounds!!, 100),
                durationMs = 1000
            )
        } else {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(
                        LatLng(cameraPosition.latitude, cameraPosition.longitude),
                        zoom
                    )
                ),
                durationMs = 800
            )
        }
    }
}

@Composable
fun HandleCameraChanges(
    modifier: Modifier = Modifier,
    cameraPositionState: CameraPositionState,
    isMapLoaded: Boolean,
    setResult: (Pair<Float, Float>) -> Unit,
) {
    LaunchedEffect(cameraPositionState, isMapLoaded) {
        snapshotFlow { cameraPositionState.isMoving }.collect { isMoving ->
            if (!isMoving) {
                val projection = cameraPositionState.projection

                projection?.let {

                    val visibleRegion = projection.visibleRegion

                    val results = FloatArray(1)

                    Location.distanceBetween(
                        visibleRegion.nearLeft.latitude,
                        visibleRegion.nearLeft.longitude,
                        visibleRegion.nearRight.latitude,
                        visibleRegion.nearRight.longitude,
                        results
                    )
                    val width = results[0]

                    Location.distanceBetween(
                        visibleRegion.nearLeft.latitude,
                        visibleRegion.nearLeft.longitude,
                        visibleRegion.farLeft.latitude,
                        visibleRegion.farLeft.longitude,
                        results
                    )
                    val height = results[0]
                    setResult(Pair(width, height))
                }
            }
        }
    }
}


fun calculateDynamicRadius(boundsDistance: Float): Double {
    // Simple logic to calculate radius dynamically to keep the same ratio when the map zoom changes
    // Numbers are chosen based on trying different values
    val baseScreenRadius = 22.0
    if (boundsDistance == 0f)
        return baseScreenRadius

    val baseDistance = 1525
    val res = boundsDistance * baseScreenRadius / baseDistance
    return res

}



