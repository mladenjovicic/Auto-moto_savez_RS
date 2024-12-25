package rs.mladenjovicic.amsrs.ui.main.web

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WebViewModel : ViewModel() {

    private val _urlLiveData = MutableLiveData<List<String>>()
    val urlLiveData: LiveData<List<String>> get() = _urlLiveData

    private val _urlList = mutableListOf<String>()

    init {
        _urlLiveData.value = _urlList
    }

    fun setInitialUrl(url: String) {
        if (_urlList.isEmpty()) {
            _urlList.add(url)
            _urlLiveData.value = _urlList
        }
    }

    fun addUrl(url: String) {
        if (_urlList.isEmpty() || _urlList.last() != url) {
            _urlList.add(url)
            _urlLiveData.value = _urlList
        }
    }

    fun removeLastUrl() {
        if (_urlList.isNotEmpty()) {
            _urlList.removeAt(_urlList.size - 1)
            _urlLiveData.value = _urlList
        }
    }

    fun clearUrls() {
        _urlList.clear()
        _urlLiveData.value = _urlList
    }

    fun extractPhoneNumber(url: String): String? {
        val regex = "tel://(\\d+)".toRegex()
        val matchResult = regex.find(url)
        return matchResult?.groupValues?.get(1)
    }

    fun extractPhoneNumber1(input: String): String? {
        val regex = """tel:(\d{3})-(\d{3})-(\d{3})""".toRegex()
        val matchResult = regex.find(input)
        return matchResult?.destructured?.let { (part1, part2, part3) ->
            "$part1$part2$part3"
        }
    }

    fun shouldOpenPdf(url: String): Boolean {
        return url.endsWith(".pdf", ignoreCase = true)
    }
}

