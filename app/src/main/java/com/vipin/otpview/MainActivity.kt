package com.vipin.otpview

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vipin.otpview.other.OTPListener


class MainActivity : AppCompatActivity() {
    private var otpTextView: OtpTextView? = null
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)







//
//        val parentLayout = findViewById<LinearLayout>(R.id.parentLayout) // Parent layout containing EditTexts
//        val boxCount = 4 // Number of input boxes
//        val editTexts = mutableListOf<EditText>() // To hold references to all boxes
//
//        // Create EditText fields dynamically
//        for (i in 0 until boxCount) {
//            val editText = EditText(this).apply {
//                layoutParams = LinearLayout.LayoutParams(
//                    0,
//                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                    1f
//                ).also { params -> params.marginEnd = 8 }
//                maxLines = 1
//                isSingleLine = true
//                filters = arrayOf(InputFilter.LengthFilter(1)) // Allow only 1 character per box
//                textAlignment = View.TEXT_ALIGNMENT_CENTER
//                setBackgroundResource(R.drawable.empty_box) // Initial background as empty
//                setPadding(16, 16, 16, 16)
//            }
//
//            editTexts.add(editText)
//            parentLayout.addView(editText)
//        }
//
//        // Add listeners to handle behavior
//        editTexts.forEachIndexed { index, editText ->
//            editText.addTextChangedListener(object : TextWatcher {
//                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                    if (!s.isNullOrEmpty()) {
//                        editText.setBackgroundResource(R.drawable.filled_box) // Change to filled background
//                        // Move to the next box if not the last
//                        if (index < boxCount - 1) {
//                            editTexts[index + 1].requestFocus()
//                        }
//                    }
//                }
//
//                override fun afterTextChanged(s: Editable?) {}
//            })
//
//            // Handle backspace navigation
//            editText.setOnKeyListener { _, keyCode, event ->
//                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
//                    if (editText.text.isEmpty() && index > 0) {
//                        // Go to the previous box if current is empty
//                        editTexts[index - 1].requestFocus()
//                        editTexts[index - 1].text.clear()
//                        editTexts[index - 1].setBackgroundResource(R.drawable.empty_box) // Change back to empty background
//                    }
//                    return@setOnKeyListener true
//                }
//                false
//            }
//        }



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.black)
        }
        val errorButton = findViewById<Button>(R.id.button)
        val successButton = findViewById<Button>(R.id.button2)
        otpTextView = findViewById(R.id.otp_view)
        otpTextView?.requestFocusOTP()
        otpTextView?.otpListener = object : OTPListener, com.vipin.otpview.OTPListener{
            override fun onInteractionListener() {

            }

            override fun onOTPComplete(otp: String) {
                Toast.makeText(this@MainActivity, "The OTP is $otp", Toast.LENGTH_SHORT).show()
            }

            override fun onOTPEntered(otpText: String) {
                TODO("Not yet implemented")
            }
        }
        errorButton.setOnClickListener { otpTextView?.showError() }
        successButton.setOnClickListener { otpTextView?.showSuccess() }
    }
}
