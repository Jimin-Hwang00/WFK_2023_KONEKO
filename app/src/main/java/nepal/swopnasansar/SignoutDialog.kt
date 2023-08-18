package nepal.swopnasansar

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText

class SignoutDialog(context: Context)
{
    private val dialog = Dialog(context)
    private lateinit var onClickListener: ButtonClickListener

    fun setOnClickListener(listener: ButtonClickListener) {
        onClickListener = listener
    }

    fun showDialog() {
        dialog.setContentView(R.layout.dialog_sign_out)
        dialog.window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.show()

        val signOutBtn = dialog.findViewById<Button>(R.id.btn_sign_out)
        val cancelBtn = dialog.findViewById<Button>(R.id.btn_sign_out_cancel)

        signOutBtn.setOnClickListener {
            val email = dialog.findViewById<EditText>(R.id.ev_sign_out_email).text.toString()
            val pw = dialog.findViewById<EditText>(R.id.ev_sign_out_pw).text.toString()

            onClickListener.onClicked(email, pw)

            dialog.dismiss()
        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
    }

    interface ButtonClickListener {
        fun onClicked(email: String, pw: String)
    }
}