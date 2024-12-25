package rs.mladenjovicic.amsrs.ui.main.web

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.findNavController
import rs.mladenjovicic.amsrs.R


class WebFragment : Fragment() {

    private lateinit var wVMain: WebView
    private val viewModel: WebViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribedUI()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_web, container, false)
    }

    private fun subscribedUI() {
        loadWebView(url = "https://m.satwork.net/ams-rs/")
    }

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    private fun loadWebView(url: String) {
        wVMain = requireView().findViewById(R.id.wVMain)
        wVMain.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
        }
        wVMain.webViewClient = object : WebViewClient() {
            override fun onPageStarted(
                view: WebView,
                url: String,
                favicon: android.graphics.Bitmap?
            ) {
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                extractPhoneNumber(url)?.let { openDialer(it) }
            }

            override fun onReceivedError(
                view: WebView, request: WebResourceRequest, error: android.webkit.WebResourceError
            ) {
                super.onReceivedError(view, request, error)
            }
        }
        wVMain.loadUrl(url)
        wVMain.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                if (wVMain.canGoBack()) {
                    wVMain.goBack()
                    return@OnKeyListener true
                }
            }
            return@OnKeyListener false
        })
        wVMain.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val hitTestResult = (v as WebView).hitTestResult
                if (hitTestResult.type == WebView.HitTestResult.SRC_ANCHOR_TYPE ||
                    hitTestResult.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE
                ) {
                    hitTestResult.extra?.let { url ->
                        if (url.endsWith(".pdf")) {
                            openPDFReader(url = url)
                            return@setOnTouchListener true
                        }
                    }
                }
            }
            false
        }
    }


    private fun openPDFReader(url: String) {
        view?.findNavController()?.navigate(
            WebFragmentDirections.actionWebFragment2ToPdfReaderFragment2().setWebLink(url)
        )
    }

    private fun extractPhoneNumber(url: String): String? {
        val regex = "tel://(\\d+)".toRegex()
        val matchResult = regex.find(url)
        return matchResult?.groupValues?.get(1)
    }

    private fun openDialer(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        startActivity(intent)
    }

}