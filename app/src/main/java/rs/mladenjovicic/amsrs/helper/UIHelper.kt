package rs.mladenjovicic.amsrs.helper

import android.content.Context
import androidx.appcompat.app.AlertDialog
import rs.mladenjovicic.amsrs.R

object UIHelper {

    fun showNoInternetDialog(context: Context, onRetry: () -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.no_interent_title))
        builder.setMessage(context.getString(R.string.no_interent_message))
        builder.setPositiveButton(context.getString(R.string.no_internet_again)) { dialog, _ ->
            dialog.dismiss()
            if (!Network.isInternetAvailable(context)) {
                showNoInternetDialog(context, onRetry)
            } else {
                onRetry()
            }
        }
        builder.setNegativeButton(context.getString(R.string.no_interenet_cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }


    fun showLocationDialog(context: Context, title:String, message:String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)

        builder.setNegativeButton(context.getString(R.string.no_interenet_cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }
}