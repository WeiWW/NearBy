package com.ann.nearby

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ann.nearby.ui.main.MainFragment
import com.mapbox.mapboxsdk.Mapbox

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        Mapbox.getInstance(
            this,
            BuildConfig.MAPBOX_ACCESS_TOKEN
        )

        if (savedInstanceState == null) {
            val mainFragment = MainFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .add(R.id.container, mainFragment, MainFragment.TAG)
                .commit()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        supportFragmentManager.findFragmentByTag(MainFragment.TAG)
            ?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}