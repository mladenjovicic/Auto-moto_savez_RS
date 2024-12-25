package rs.mladenjovicic.amsrs

import android.app.Application

class AMSRS : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: AMSRS
    }

}