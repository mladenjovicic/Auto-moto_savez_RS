package rs.mladenjovicic.amsrs.ui.main.pdfReader

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.os.ParcelFileDescriptor
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import rs.mladenjovicic.amsrs.R
import java.io.File
import okhttp3.Request
import java.util.concurrent.TimeUnit


class PdfReaderFragment : Fragment() {
    private val args: PdfReaderFragmentArgs by navArgs()
    private lateinit var recyclerViewPDFReader: RecyclerView
    private val pdfPages = mutableListOf<Bitmap>()


    companion object {
        fun newInstance() = PdfReaderFragment()
    }

    private val viewModel: PdfReaderViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribedUI()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_pdf_reader, container, false)
    }


    private fun subscribedUI() {
        pdfReader(args.webLink)
    }

    private fun pdfReader(url: String) {
        recyclerViewPDFReader = requireView().findViewById(R.id.recyclerViewPDFReader)
        recyclerViewPDFReader.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewPDFReader.adapter = PdfAdapter(pdfPages)

        CoroutineScope(Dispatchers.IO).launch {
            val pdfFile = downloadPdf(url = url)
            renderPdfPages(pdfFile)
            withContext(Dispatchers.Main) {
                recyclerViewPDFReader.adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun downloadPdf(url: String): File {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()

        val file = File(requireContext().cacheDir, "amsrs_cache.pdf")
        response.body?.byteStream()?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    private fun renderPdfPages(pdfFile: File) {
        val parcelFileDescriptor =
            ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
        val pdfRenderer = PdfRenderer(parcelFileDescriptor)

        for (i in 0 until pdfRenderer.pageCount) {
            val page = pdfRenderer.openPage(i)
            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            pdfPages.add(bitmap)
            page.close()
        }
        pdfRenderer.close()
        parcelFileDescriptor.close()
    }
}