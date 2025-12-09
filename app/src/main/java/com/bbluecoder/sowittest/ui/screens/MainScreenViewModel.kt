package com.bbluecoder.sowittest.ui.screens

import android.annotation.SuppressLint
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bbluecoder.sowittest.data.PlotsRepository
import com.bbluecoder.sowittest.db.MPolygon
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface PolygonUiState {
    object Loading : PolygonUiState
    data class Success(
        val polygons: List<MPolygon>
    ) : PolygonUiState
}

@Suppress("DEPRECATION")
@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val repository: PlotsRepository
) : ViewModel() {

    private val THRESHOLD_CLICK = 50
    private val defaultMapCameraPosition =
        LatLng(33.571034068263444, -7.671464242133383) // Casablanca

    private val _points = MutableStateFlow<List<LatLng>>(emptyList())
    val points = _points.asStateFlow()

    private var locationClient: FusedLocationProviderClient? = null

    private var isPolygonLocationRetrieved = false

    var polygonLocationCity = ""
        private set

    var isMapCameraAnimationEnabled = MutableStateFlow(false)
        private set

    var cameraTarget = MutableStateFlow(defaultMapCameraPosition)
        private set

    var isDrawing = MutableStateFlow(false)
        private set

    var showPolygonDetailsDialog = MutableStateFlow(false)
        private set

    var polygonColor = MutableStateFlow(Constants.colorsPallet.random())
        private set

    var showPolygonPointsError = MutableStateFlow(false)
        private set

    var bounds = MutableStateFlow<LatLngBounds?>(null)
        private set

    var isPolygonAnimating = false
        private set


    val uiState: StateFlow<PolygonUiState> =
        repository.getPolygons().map { polygons ->
            PolygonUiState.Success(polygons)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PolygonUiState.Loading
        )

    fun setLocationClient(locationClient: FusedLocationProviderClient) {
        this.locationClient = locationClient
    }

    fun savePolygon(name: String, city: String, color: Color) {
        showPolygonDetailsDialog.value = false
        if (color != polygonColor.value) {
            polygonColor.value = color
        }
        viewModelScope.launch {
            repository.addPolygon(
                name = name,
                city = city,
                color = color,
                points = _points.value
            )
        }
    }

    fun handlePlotItemClick(polygon: MPolygon) {
        if(isDrawing.value)
            return

        val points = polygon.points.map { LatLng(it.lat, it.lng) }
        _points.value = points
        isMapCameraAnimationEnabled.value = true
        polygonColor.value = polygon.polygon.color
        isPolygonAnimating = true
        bounds.value = calculateBounds(points)
    }

    @SuppressLint("MissingPermission")
    fun fetchLocation() {
        isPolygonAnimating = false
        isMapCameraAnimationEnabled.value = false
        cameraTarget.value = LatLng(0.0, 0.0)
        isMapCameraAnimationEnabled.value = true
        if(locationClient == null) {
            cameraTarget.value = defaultMapCameraPosition
        }else {
            locationClient?.lastLocation?.addOnSuccessListener { location ->
                isMapCameraAnimationEnabled.value = true
                cameraTarget.value = LatLng(location.latitude, location.longitude)
            }
        }
    }

    fun drawPolygon() {
        if (_points.value.size < 3){
            showPolygonPointsError.value = true
            return
        }
        isDrawing.value = false
        showPolygonDetailsDialog.value = true
    }

    fun hidePolygonPointsError() {
        showPolygonPointsError.value = false
    }

    fun cancelDrawing() {
        _points.value = emptyList()
        isDrawing.value = false
        showPolygonDetailsDialog.value = false
    }

    fun handleMapOnClick(latLng: LatLng, geocoder: Geocoder) {
        if (isDrawing.value) {
            _points.value.forEach {
                if (isClickCloseToAnExistingPoint(latLng, it)) {
                    drawPolygon()
                    return
                }
            }
            addPoint(latLng, geocoder)
        }
    }

    fun newPolygon() {
        _points.value = emptyList()
        isDrawing.value = true
        isPolygonLocationRetrieved = false
        polygonColor.value = Constants.colorsPallet.random()
        polygonLocationCity = ""
    }

    fun addPoint(point: LatLng, geocoder: Geocoder) {
        _points.update { it + point }
        if (!isPolygonLocationRetrieved)
            getCityNameFromLocation(point, geocoder) // Retrieve the location of the polygon,
        // so the city name is available when saving the polygon

    }

    // In case the user click wants to finish drawing by clicking on an existing point
    private fun isClickCloseToAnExistingPoint(click: LatLng, point: LatLng): Boolean {
        val distance = FloatArray(1)
        Location.distanceBetween(
            click.latitude,
            click.longitude,
            point.latitude,
            point.longitude,
            distance
        )
        return distance.first() <= THRESHOLD_CLICK
    }

    private fun getCityNameFromLocation(location: LatLng, geocoder: Geocoder) {
        if (Build.VERSION.SDK_INT >= 33) {
            geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
                val address = addresses.firstOrNull()
                val place = address?.locality ?: address?.subAdminArea ?: address?.adminArea
                ?: address?.countryName ?: ""
                polygonLocationCity = place
                isPolygonLocationRetrieved = true
            }
        } else {
            val address =
                geocoder.getFromLocation(location.latitude, location.longitude, 1)?.firstOrNull()
                    ?: return
            val place =
                address.locality ?: address.subAdminArea ?: address.adminArea ?: address.countryName
                ?: return
            polygonLocationCity = place
            isPolygonLocationRetrieved = true
        }
    }

    private fun calculateBounds(points: List<LatLng>): LatLngBounds {
        val builder = LatLngBounds.builder()
        points.forEach {
            builder.include(it)
        }
        return builder.build()
    }
}