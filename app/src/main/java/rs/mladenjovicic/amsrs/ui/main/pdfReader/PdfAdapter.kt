package rs.mladenjovicic.amsrs.ui.main.pdfReader

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import rs.mladenjovicic.amsrs.R

class PdfAdapter(private val pages: List<Bitmap>) :
    RecyclerView.Adapter<PdfAdapter.PdfViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_pdf_page, parent, false)
        return PdfViewHolder(view)
    }

    override fun onBindViewHolder(holder: PdfViewHolder, position: Int) {
        holder.bind(pages[position])
    }

    override fun getItemCount(): Int = pages.size

    class PdfViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.pdf_page_image)

        fun bind(bitmap: Bitmap) {
            imageView.setImageBitmap(bitmap)
        }
    }
}