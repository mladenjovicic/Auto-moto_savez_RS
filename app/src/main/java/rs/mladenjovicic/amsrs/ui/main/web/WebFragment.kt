package rs.mladenjovicic.amsrs.ui.main.web

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import rs.mladenjovicic.amsrs.R


class WebFragment : Fragment() {

    private lateinit var wVMain: WebView
    private lateinit var loadingSpinner: ProgressBar
    private val viewModel: WebViewModel by viewModels()
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_web, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wVMain = view.findViewById(R.id.wVMain)
        loadingSpinner = view.findViewById(R.id.loadingSpinner)

        viewModel.setInitialUrl("https://m.satwork.net/ams-rs/")

        setupWebView()
        backButton()
        viewModel.urlLiveData.observe(requireActivity(), Observer { urlList ->
            Log.d("WebViewActivity", "Lista URL-ova: ${urlList.joinToString(", ")}")
        })
        viewModel.urlLiveData.observe(viewLifecycleOwner) { urlList ->
            if (urlList.isNotEmpty()) {
                val lastUrl = urlList.last()
                if (lastUrl != wVMain.url) {
                    wVMain.loadUrl(lastUrl)
                }
            }
        }
    }

    private fun backButton() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (isLoading) {
                        return
                    }
                    viewModel.removeLastUrl()
                    if (viewModel.urlLiveData.value.isNullOrEmpty()) {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    } else {
                        viewModel.urlLiveData.value?.lastOrNull()?.let { lastUrl ->
                            wVMain.loadUrl(lastUrl)
                        }
                    }
                }
            }
        )
    }

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    private fun setupWebView() {
        wVMain.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
        }
        var lastUrl: String? = null

        wVMain.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                val phoneNumber = viewModel.extractPhoneNumber(url!!)
                val extractPhoneNumber1 = viewModel.extractPhoneNumber1(url!!)
                if (phoneNumber == null && extractPhoneNumber1 == null) {
                    if (url != "https://satwork.net/") {
                        isLoading = true
                        loadingSpinner.visibility = View.VISIBLE
                        viewModel.addUrl(url)
                        Log.d("WebViewActivity", "URL-ova start res: ${url}")
                    } else {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(url)
                        }
                        startActivity(intent)
                    }
                }
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                if (url != lastUrl) {
                    lastUrl = url
                    val extractPhoneNumber = viewModel.extractPhoneNumber(url)
                    val extractPhoneNumber1 = viewModel.extractPhoneNumber1(url)

                    if (extractPhoneNumber != null) {
                        openDialer(extractPhoneNumber)
                        wVMain.visibility = View.GONE
                    } else if (extractPhoneNumber1 != null) {
                        Log.d("WebViewActivity", " number 1: ${extractPhoneNumber1}")
                        openDialer(extractPhoneNumber1)
                        wVMain.visibility = View.GONE
                    } else {
                        if (url != "https://satwork.net/") {
                            viewModel.addUrl(url)
                        }
                        Log.d("WebViewActivity", "URL-ova finish res: ${url}")
                        wVMain.visibility = View.VISIBLE
                    }
                }
                loadingSpinner.visibility = View.GONE

                isLoading = false

            }
        }
        wVMain.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val hitTestResult = (v as WebView).hitTestResult
                if (hitTestResult.type == WebView.HitTestResult.SRC_ANCHOR_TYPE ||
                    hitTestResult.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE
                ) {
                    hitTestResult.extra?.let { url ->
                        if (viewModel.shouldOpenPdf(url)) {
                            openPDFReader(url)
                            Log.d("WebViewActivity", "URL-ova hit res: ${url}")
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

    private fun openDialer(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        viewModel.urlLiveData.value?.lastOrNull()?.let { lastUrl ->
            wVMain.loadUrl(lastUrl)
        }
    }
}
