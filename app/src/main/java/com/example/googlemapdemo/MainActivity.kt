package com.example.googlemapdemo

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.example.googlemapdemo.Utils.hideSoftKeyBoard
import com.example.googlemapdemo.Utils.isGpsEnable
import com.example.googlemapdemo.adapter.DirectionAdapter
import com.example.googlemapdemo.adapter.SearchAdapter
import com.example.googlemapdemo.model.DirectionResponse
import com.example.googlemapdemo.model.GeometryData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var adapter: DirectionAdapter
    private lateinit var rvDirections: RecyclerView
    private lateinit var tvNoWay: TextView
    private lateinit var edtSearch: EditText
    private lateinit var btnDirect: Button
    private lateinit var tvRouteInfo: TextView
    private lateinit var rvSearch: RecyclerView


    private lateinit var searchAdapter: SearchAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Pair<String, LatLng>? = null
    private var currentPolyline: Polyline? = null
    private var secondPolyline: Polyline? = null
    private var mapFragment: SupportMapFragment? = null
    private var viewModel = MainViewModel()

    // Create a new PlacesClient instance
    private lateinit var placesClient: PlacesClient

    // Use fields to define the data types to return.
    private val placeLatLngFields: List<Place.Field> = listOf(Place.Field.LAT_LNG)

    // Use the builder to create a FindCurrentPlaceRequest.
    private val request: FindCurrentPlaceRequest =
        FindCurrentPlaceRequest.newInstance(placeLatLngFields)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        handleEvents()
        observe()
    }

    private fun handleEvents() {
        edtSearch.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty() || currentLocation?.first?.trim() == text.toString()
                    .trim()
            ) return@doOnTextChanged
            viewModel.viewModelScope.launch {
                viewModel.textChangeSharedFlow.emit(text.trim().toString())
            }
        }

        edtSearch.setOnEditorActionListener(OnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (v.text.isNullOrEmpty()) return@OnEditorActionListener false
                viewModel.searchPlace(v.text.toString())
                return@OnEditorActionListener true
            }
            false
        })

        btnDirect.setOnClickListener {
            directFromHereTo(currentLocation?.second)
        }
    }

    @SuppressLint("MissingPermission")
    private fun directFromHereTo(destination: LatLng?) {
        if (destination == null) return
        currentPolyline?.remove()
        secondPolyline?.remove()
        if (isGpsEnable())
            enableMyLocation {
                fusedLocationClient.lastLocation.addOnSuccessListener {
                    viewModel.getDirectionsFromTo(LatLng(it.latitude, it.longitude), destination)
                }
            }
        else Toast.makeText(this, getString(R.string.please_turn_on_gps), Toast.LENGTH_SHORT)
            .show()
    }

    private fun observe() {
        viewModel.directionState.observe(this) {
            if (it.isSuccess()) {
                updateDirectionsViews(it.data)
                updateCameraBound(it.data)
            } else if (it.isError()) {
                Toast.makeText(this, "ERROR: ${it.throwable}", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.searchPlaceState.observe(this) {
            if (it.isSuccess()) {
                searchAdapter.updateData(it.data?.results ?: emptyList()) { show ->
                    rvSearch.isVisible = !show
                }
            } else if (it.isError()) {
                Toast.makeText(this, "ERROR: ${it.throwable}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateCameraBound(direction: DirectionResponse?) {
        val latLngBounds = LatLngBounds.Builder()
        direction?.routes?.firstOrNull()?.legs?.firstOrNull()?.steps?.forEach { step ->
            val start = step.startLocation
            val end = step.endLocation
            if (start != null && end != null) {
                latLngBounds.include(LatLng(start.latitude, start.longitude))
                latLngBounds.include(LatLng(end.latitude, end.longitude))
            }
        } ?: return
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), 70))
    }

    private fun getPolyline(isNearestRoute: Boolean): PolylineOptions {
        val color =
            ContextCompat.getColor(this, if (isNearestRoute) R.color.blue_60 else R.color.brown_40)
        return PolylineOptions().width(POLYLINE_WIDTH).apply {
            color(color)
            jointType(JointType.ROUND)
        }
    }

    private fun updateDirectionsViews(direction: DirectionResponse?) {
        val route = direction?.routes?.firstOrNull()
        tvRouteInfo.text = getString(
            R.string.route_info,
            route?.legs?.firstOrNull()?.startAddressName,
            route?.legs?.firstOrNull()?.endAddressName,
            route?.legs?.firstOrNull()?.distance?.text,
            route?.legs?.firstOrNull()?.duration?.text
        )
        if (route == null) {
            Toast.makeText(this, getString(R.string.not_found_any_way), Toast.LENGTH_SHORT).show()
            return
        }
        val decodedPath =
            PolyUtil.decode(route.overviewPolyline?.encodedPolyline)
        currentPolyline = map.addPolyline(getPolyline(true).addAll(decodedPath))
        adapter.updateData(
            route.legs?.firstOrNull()?.steps ?: emptyList()
        ) { isEmpty ->
            tvNoWay.isVisible = isEmpty
        }

        direction.routes.getOrNull(1)?.let {
            val decodedPath2 =
                PolyUtil.decode(it.overviewPolyline?.encodedPolyline)
            secondPolyline = map.addPolyline(getPolyline(false).addAll(decodedPath2))
        }
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(REQUEST_CODE_LOCATION)
    private fun initViews() {
        setupViews()
        mapFragment = supportFragmentManager.findFragmentById(
            R.id.map_fragment
        ) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        adapter = DirectionAdapter()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        var currentMarker: Marker? = null
        searchAdapter = SearchAdapter({ geo, name ->
            if (geo.location != null) {
                currentLocation = Pair(name, LatLng(geo.location.latitude, geo.location.longitude))
                edtSearch.setText(name)
                edtSearch.hideSoftKeyBoard()
                currentMarker?.remove()
                currentMarker =
                    addMarker(LatLng(geo.location.latitude, geo.location.longitude), geo.viewPort)
                btnDirect.visibility = View.VISIBLE
                rvSearch.visibility = View.GONE
            }
        }) {
            edtSearch.setText(it)
        }
        rvDirections.adapter = adapter
        rvSearch.adapter = searchAdapter

        findCurrentPlaces()
    }

    private fun setupViews() {
        rvDirections = findViewById(R.id.rvDirection)
        rvSearch = findViewById(R.id.rvSearch)
        edtSearch = findViewById(R.id.edtSearch)
        btnDirect = findViewById(R.id.btnDirect)
        tvNoWay = findViewById(R.id.tvNoWay)
        tvRouteInfo = findViewById(R.id.tvRouteInfo)
    }

    @SuppressLint("MissingPermission")
    private fun findCurrentPlaces() {
        Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
        placesClient = Places.createClient(this)
        enableMyLocation {
            val placeResponse = placesClient.findCurrentPlace(request)
            placeResponse.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("AROUND LOCATION:", "${task.result.placeLikelihoods}")
                } else {
                    Toast.makeText(
                        this,
                        "FIND PLACE ERROR: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(REQUEST_CODE_LOCATION)
    override fun onMapReady(map: GoogleMap) {
        this.map = map
        enableMyLocation {
            this.map.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it?.longitude == null) return@addOnSuccessListener
                this.map.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 15f)
                )
            }
        }
        map.uiSettings.isMyLocationButtonEnabled = true
        val area = LatLng(16.131868, 108.116873)
        addMarker(area)
        map.setOnMapClickListener {
            Log.d("MAP CLICK", "${it.latitude}, ${it.longitude}")
        }
    }

    // move camera to a location
    private fun addMarker(location: LatLng, viewPort: GeometryData.ViewPortData? = null): Marker? {
        viewPort?.let {
            val australiaBounds = LatLngBounds(
                LatLng(it.southwest.latitude, it.southwest.longitude),  // SW bounds
                LatLng(it.northeast.latitude, it.northeast.longitude) // NE bounds
            )
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(australiaBounds.center, 17f))
        }

        return map.addMarker(
            MarkerOptions()
                .position(location)
                .title("Favourite")
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun enableMyLocation(onPermissionGranted: () -> Unit) {
        if (hasLocationPermission()) {
            onPermissionGranted()
        } else {
            EasyPermissions.requestPermissions(
                this, getString(R.string.grant_location_description),
                REQUEST_CODE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    private fun hasLocationPermission(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    companion object {
        private const val REQUEST_CODE_LOCATION = 63
        private const val POLYLINE_WIDTH = 5f
    }
}
