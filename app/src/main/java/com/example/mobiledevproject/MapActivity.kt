package com.example.mobiledevproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        val location = LatLng(45.5017, -73.5673) // exemple Montréal
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14f))

        // Exemple de restaurant proche
        val restaurant = LatLng(45.503, -73.57)
        googleMap.addMarker(MarkerOptions().position(restaurant).title("Restaurant X"))

        // TODO: ajouter le marqueur à la consommation d’un aliment
    }
}