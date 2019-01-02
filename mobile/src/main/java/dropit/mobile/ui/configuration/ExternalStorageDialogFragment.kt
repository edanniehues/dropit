package dropit.mobile.ui.configuration

import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.support.v7.app.AlertDialog
import dropit.mobile.R

class ExternalStorageDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            AlertDialog.Builder(it)
                .setMessage(R.string.external_storage_permission_explanation)
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    dialog.dismiss()
                    (activity as ConfigurationActivity).requestExternalStoragePermission()
                }.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}