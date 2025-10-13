package com.example.guideme

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.guideme.tts.TTS
import android.app.AlertDialog


fun openPhoneApp(context: Context) {
    // Speak instructions first
    TTS.speak("I will now open your phone app. You can make a call by selecting a contact or typing a number.")

    // Intent to open the phone dialer
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data = Uri.parse("tel:") // Opens empty dialer
    context.startActivity(intent)

    val builder = AlertDialog.Builder(context)
    builder.setTitle("This is a pop-up alert")
    builder.setMessage("Press OK to continue.")
    builder.setPositiveButton("OK") { dialog, _ ->
        dialog.dismiss()
    }
    builder.show()
}
