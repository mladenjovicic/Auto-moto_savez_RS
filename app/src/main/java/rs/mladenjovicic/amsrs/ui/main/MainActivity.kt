package rs.mladenjovicic.amsrs.ui.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import rs.mladenjovicic.amsrs.R
import android.location.Location
import android.location.LocationManager
import rs.mladenjovicic.amsrs.helper.UIHelper
import android.location.LocationListener


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbarMain))
        subscribedUI()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.locationButton -> {
                getCurrentLocation()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun subscribedUI() {
        requestPermissionsLocation()
    }

    private fun requestPermissionsLocation() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
        }
    }

    private fun getCurrentLocation() {
        requestPermissionsLocation()
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            var gpsLocation: Location?
            var networkLocation: Location?
            var locationReceived = false


            val gpsListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    if (!locationReceived) {
                        gpsLocation = location
                        locationReceived = true
                        locationManager.removeUpdates(this)
                        displayLocation(gpsLocation, "GPS")
                    }
                }

                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }

            val networkListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    if (!locationReceived) {
                        networkLocation = location
                        locationReceived = true
                        locationManager.removeUpdates(this)
                        displayLocation(networkLocation, "Mobilne mreze")
                    }
                }

                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0L,
                    0f,
                    gpsListener
                )
            }
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0L,
                    0f,
                    networkListener
                )
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Permisija za lokacija nisu odobrene", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayLocation(gpsLoc: Location?, source: String) {
        UIHelper.showLocationDialog(
            context = this,
            title = "Lokacija sa ${source}",
            message = "lat: ${gpsLoc?.latitude}, lon: ${gpsLoc?.longitude}, acc: ${gpsLoc?.accuracy}"
        )

    }

}