package rs.mladenjovicic.amsrs.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rs.mladenjovicic.amsrs.R
import rs.mladenjovicic.amsrs.helper.Network
import rs.mladenjovicic.amsrs.helper.UIHelper
import rs.mladenjovicic.amsrs.ui.main.MainActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lifecycleScope.launch {
            delay(2000)
            subscribedUI()
        }
    }


    private fun subscribedUI() {

        val openMainActivity = {
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()

        }
        if (!Network.isInternetAvailable(this)) {
            UIHelper.showNoInternetDialog(this) {
                if (Network.isInternetAvailable(this)) openMainActivity()
            }
        } else {
            openMainActivity()
        }
    }
}