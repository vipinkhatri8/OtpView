package com.vipin.otpview


import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import java.util.*
import java.util.regex.Pattern

class OtpTextView : FrameLayout {

    private var itemViews: MutableList<ItemView>? = null
    private var otpChildEditText: OTPChildEditText? = null
    var otpListener: OTPListener? = null

    private var length: Int = 0
    private var textColor: Int = Color.BLACK

    private val filter: InputFilter
        get() = InputFilter { source, start, end, _, _, _ ->
            for (i in start until end) {
                if (!Pattern.compile(
                        PATTERN)
                        .matcher(source[i].toString())
                        .matches()) {
                    return@InputFilter ""
                }
            }
            null
        }

    val otp: String?
        get() = otpChildEditText?.text?.toString()

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val styles = context.obtainStyledAttributes(attrs, R.styleable.OtpTextView)
        textColor = styles.getColor(R.styleable.OtpTextView_android_textColor, ResourcesCompat.getColor(context.resources, R.color.black, null))


        styleEditTexts(styles, attrs)
        styles.recycle()
    }


//    private fun styleEditTexts(styles: TypedArray, attrs: AttributeSet?) {
//        length = styles.getInt(R.styleable.OtpTextView_length, DEFAULT_LENGTH)
//        generateViews(styles, attrs)
//    }


//    private fun styleEditTexts(styles: TypedArray, attrs: AttributeSet?) {
//        length = styles.getInt(R.styleable.OtpTextView_length, DEFAULT_LENGTH)
//        textColor = styles.getColor(
//            R.styleable.OtpTextView_android_textColor,
//            ResourcesCompat.getColor(context.resources, R.color.black, null)
//        )
//        generateViews(styles, attrs)
//    }

    private fun styleEditTexts(styles: TypedArray, attrs: AttributeSet?) {
        length = styles.getInt(R.styleable.OtpTextView_length, DEFAULT_LENGTH)
        textColor = styles.getColor(
            R.styleable.OtpTextView_android_textColor,
            ResourcesCompat.getColor(context.resources, R.color.black, null) // Default color
        )
        val itemTextColor = styles.getColor(
            R.styleable.OtpTextView_itemTextColor,
            ResourcesCompat.getColor(context.resources, R.color.black, null)
        )
        generateViews(styles, attrs,itemTextColor)
    }


    private fun generateViews(styles: TypedArray, attrs: AttributeSet?,itemTextColor: Int) {
        itemViews = ArrayList()
        if (length > 0) {
            val otp = styles.getString(R.styleable.OtpTextView_otp)
            val width = styles.getDimension(R.styleable.OtpTextView_width, Utils.getPixels(context, DEFAULT_WIDTH).toFloat()).toInt()
            val height = styles.getDimension(R.styleable.OtpTextView_height, Utils.getPixels(context, DEFAULT_HEIGHT).toFloat()).toInt()
            val space = styles.getDimension(R.styleable.OtpTextView_box_margin, Utils.getPixels(context, DEFAULT_SPACE).toFloat()).toInt()
            val spaceLeft = styles.getDimension(R.styleable.OtpTextView_box_margin_left, Utils.getPixels(context, DEFAULT_SPACE_LEFT).toFloat()).toInt()
            val spaceRight = styles.getDimension(R.styleable.OtpTextView_box_margin_right, Utils.getPixels(context, DEFAULT_SPACE_RIGHT).toFloat()).toInt()
            val spaceTop = styles.getDimension(R.styleable.OtpTextView_box_margin_top, Utils.getPixels(context, DEFAULT_SPACE_TOP).toFloat()).toInt()
            val spaceBottom = styles.getDimension(R.styleable.OtpTextView_box_margin_bottom, Utils.getPixels(context, DEFAULT_SPACE_BOTTOM).toFloat()).toInt()
            val otpMatchParent = styles.getBoolean(R.styleable.OtpTextView_otp_box_match_parent, false)
            val params = if (otpMatchParent) LinearLayout.LayoutParams(width, height, 1f)
            else LinearLayout.LayoutParams(width, height)
            if (space > 0) {
                params.setMargins(space, space, space, space)
            } else {
                params.setMargins(spaceLeft, spaceTop, spaceRight, spaceBottom)
            }

            val editTextLayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            editTextLayoutParams.gravity = Gravity.CENTER
            otpChildEditText = OTPChildEditText(context)
            otpChildEditText?.filters = arrayOf(filter, InputFilter.LengthFilter(length))
            setTextWatcher(otpChildEditText)
            setFocusDetection(otpChildEditText)
            addView(otpChildEditText, editTextLayoutParams)


            val linearLayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val linearLayout = LinearLayout(context)

            addView(linearLayout, linearLayoutParams)

            val textColor = styles.getColor(
                R.styleable.OtpTextView_android_textColor,
                ResourcesCompat.getColor(context.resources, R.color.black, null)
            )

//            for (i in 0 until length) {
//                val itemView = ItemView(context, attrs)
//                itemView.setTextColor(textColor)
//                itemView.setViewState(ItemView.INACTIVE)
//                linearLayout.addView(itemView, i, params)
//                itemViews?.add(itemView)
//            }

            for (i in 0 until length) {
                val itemView = ItemView(context, attrs)
                itemView.setTextColor(itemTextColor) // Apply text color to each box
                itemView.setViewState(ItemView.INACTIVE)
                linearLayout.addView(itemView, i, params)
                itemViews?.add(itemView)
            }

            if (otp != null) {
                setOTP(otp)
            } else {
                setOTP("")
            }
        } else {
            throw IllegalStateException("Please specify the length of the otp view")
        }
    }

    private fun setTextWatcher(otpChildEditText: OTPChildEditText?) {
        otpChildEditText?.addTextChangedListener(object : TextWatcher {
            /**
             * @param s
             * @param start
             * @param count
             * @param after
             */
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            /**
             * @param s
             * @param start
             * @param before
             * @param count
             */
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setOTP(s)
                setFocus(s.length)
                otpListener?.let { otpListener ->
                    otpListener.onInteractionListener()
                    if (s.length == length) {
                        otpListener.onOTPComplete(s.toString())
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }

    /**
     * added by pth
     */
    private fun setFocusDetection(otpChildEditText: OTPChildEditText?) {
        otpChildEditText?.onFocusChangeListener = OnFocusChangeListener { view, b ->
            if (view.isFocused) {
                otp?.let {
                    setFocus(it.length)
                }
            } else {
                otp?.let {
                    setFocus(-1)
                }
            }
        }
    }

    /**
     * modified by pth
     */

    /*
    private fun setFocus(length: Int) {
        itemViews?.let { itemViews ->
            for (i in itemViews.indices) {
                if (i == length) {
                    if (otpChildEditText?.isFocused == true)
                        itemViews[i].setViewState(ItemView.ACTIVE)
                    else itemViews[i].setViewState(ItemView.INACTIVE)
                }

                else {
                    itemViews[i].setViewState(ItemView.INACTIVE)
                }

            }
            if (length == itemViews.size) {
                itemViews[itemViews.size - 1].setViewState(ItemView.ACTIVE)
            }
        }
    }

     */

    fun setFocus(length: Int) {
        itemViews?.let { views ->
            views.forEachIndexed { index, itemView ->
                if (index < length) {
                    itemView.setViewState(ItemView.FILLED) // Filled state for entered characters
                } else if (index == length) {
                    itemView.setViewState(ItemView.ACTIVE) // Active for the current box
                } else {
                    itemView.setViewState(ItemView.INACTIVE) // Inactive for unfilled boxes
                }
            }
        }
    }


    /*
    fun setOTP(s: CharSequence) {
        itemViews?.let { itemViews ->
            for (i in itemViews.indices) {
                if (i < s.length) {
                    itemViews[i].setText(s[i].toString())
                } else {
                    itemViews[i].setText("")
                }
            }
        }
    }

     */

//vipin
fun setOTP(s: CharSequence) {
    itemViews?.let { views ->
        views.forEachIndexed { index, itemView ->
            if (index < s.length) {
                itemView.setText(s[index].toString())
                itemView.setViewState(ItemView.FILLED)
            } else {
                itemView.setText("")
                itemView.setViewState(ItemView.INACTIVE)
            }
        }
    }
}



    fun requestFocusOTP() {
        otpChildEditText?.requestFocus()
    }

    fun showError() {
        itemViews?.let { itemViews ->
            for (itemView in itemViews) {
                itemView.setViewState(ItemView.ERROR)
            }
        }
    }

    fun resetState() {
        otp?.let {
            setFocus(it.length)
        }
    }

    fun showSuccess() {
        itemViews?.let { itemViews ->
            for (itemView in itemViews) {
                itemView.setViewState(ItemView.SUCCESS)
            }
        }
    }

    fun setOTP(otp: String) {
        otpChildEditText?.setText(otp)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setOnTouchListener(l: OnTouchListener) {
        super.setOnTouchListener(l)
        otpChildEditText?.setOnTouchListener(l)
    }

    companion object {

        private const val DEFAULT_LENGTH = 4
        private const val DEFAULT_HEIGHT = 48
        private const val DEFAULT_WIDTH = 48
        private const val DEFAULT_SPACE = -1
        private const val DEFAULT_SPACE_LEFT = 4
        private const val DEFAULT_SPACE_RIGHT = 4
        private const val DEFAULT_SPACE_TOP = 4
        private const val DEFAULT_SPACE_BOTTOM = 4

        private const val PATTERN = "[1234567890]*"
    }
}