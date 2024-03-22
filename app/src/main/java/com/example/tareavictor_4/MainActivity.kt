package com.example.tareavictor_4


import android.view.animation.Animation
import android.view.animation.TranslateAnimation

import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.BounceInterpolator
import android.widget.*
import androidx.appcompat.widget.SwitchCompat

class MainActivity : AppCompatActivity() {
    private lateinit var toggleButton: ToggleButton
    private lateinit var switchButton: SwitchCompat
    private lateinit var imageView: ImageView
    private lateinit var imageButton: ImageButton
    private lateinit var scrollView: ScrollView
    private lateinit var scrollContent: LinearLayout
    private lateinit var generateTextButton: Button
    private lateinit var textView: TextView
    private lateinit var listView: ListView

    private lateinit var controlNameTextView: TextView
    private lateinit var prevButton: Button
    private lateinit var nextButton: Button

    private val controls = mutableListOf<View>()
    private val controlNames = mutableListOf<String>()
    private var currentPosition = 0

    private var isDayMode = true
    private lateinit var animationDrawable: AnimationDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        toggleButton = findViewById(R.id.toggleButton)
        switchButton = findViewById(R.id.switchButton)
        imageView = findViewById(R.id.imageView)
        imageButton = findViewById(R.id.imageButton)
        scrollView = findViewById(R.id.scrollView)
        textView = findViewById(R.id.textView)
        listView = findViewById(R.id.listView)
        controlNameTextView = findViewById(R.id.controlNameTextView)
        prevButton = findViewById(R.id.prevButton)
        nextButton = findViewById(R.id.nextButton)

        scrollContent = findViewById(R.id.scrollContent)
        generateTextButton = findViewById(R.id.generateTextButton)


        generateTextButton.setOnClickListener {
            generateLoremIpsumText()
        }

        //imageButton.setImageResource(R.drawable.redball)

        // Add controls to the list
        controls.addAll(listOf(toggleButton, switchButton, imageView, imageButton, scrollView, textView, listView))
        controlNames.addAll(listOf("Toggle Button", "Switch", "ImageView", "ImageButton", "ScrollView", "TextView", "ListView"))

        // Set initial control name
        controlNameTextView.text = controlNames[currentPosition]

        // Hide all controls except the first one
        controls.forEachIndexed { index, view ->
            view.visibility = if (index == 0) View.VISIBLE else View.GONE
        }

        // Set listener for toggle button
        toggleButton.setOnCheckedChangeListener { _, isChecked ->
            isDayMode = isChecked
            updateColors()

        }


        // Set listener for switch button
        switchButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                startBackgroundAnimation()
            } else {
                stopBackgroundAnimation()
                updateColors()
            }
        }

        imageButton.setOnClickListener {

            if (imageButton.translationY == 0f) {
                val screenHeight = resources.displayMetrics.heightPixels // Get screen height
                val distanceToBottom = screenHeight - imageButton.bottom // Calculate distance to bottom
                fallToBottom(imageButton, distanceToBottom)
            } else {
                resetPosition(imageButton)
            }


        }





    }

    private fun generateLoremIpsumText() {
        val loremIpsum = resources.getString(R.string.lorem_ipsum)
        val words = loremIpsum.split("\\s+".toRegex()) // Split text into words

        // Start index for fading in text
        var index = 0
        val duration = 100L // Duration for fading animation

        val handler = Handler()

        // Iterate over each word
        for (word in words) {
            // Create a new TextView for the word
            val textView = createTextView(word)
            textView.alpha = 0f // Initially set alpha to 0 (invisible)
            scrollContent.addView(textView) // Add TextView to the scroll content

            // Delay each word's animation
            handler.postDelayed({
                // Fade in animation
                val fadeIn = AlphaAnimation(0f, 1f)
                fadeIn.duration = duration
                textView.startAnimation(fadeIn)
                textView.alpha = 1f // Set alpha to fully visible after animation
            }, index * duration)

            index++
        }
    }


    private fun createTextView(text: String): TextView {
        val textView = TextView(this)
        textView.text = text
        textView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return textView
    }


    private fun updateColors() {
        val backgroundColor = if (isDayMode) android.R.color.white else android.R.color.black
        val buttonColor = if (isDayMode) android.R.color.holo_blue_light else android.R.color.holo_orange_light
        val textColor = if (isDayMode) android.R.color.black else android.R.color.white

        // Update background and button colors
        findViewById<View>(android.R.id.content).setBackgroundColor(resources.getColor(backgroundColor))
        findViewById<View>(R.id.prevButton).setBackgroundColor(resources.getColor(buttonColor))
        findViewById<View>(R.id.nextButton).setBackgroundColor(resources.getColor(buttonColor))
        controlNameTextView.setTextColor(resources.getColor(textColor))

        // Update text color of TextViews, Buttons, and other text-based views
        controls.filterIsInstance(TextView::class.java).forEach { it.setTextColor(resources.getColor(textColor)) }
    }

    private fun fallToBottom(imageButton: ImageButton, distanceToBottom: Int) {
        val animation = TranslateAnimation(0f, 0f, 0f,  distanceToBottom.toFloat()) // Define the animation
        animation.duration = 3000 // Set the duration of the animation
        animation.fillAfter = true // Maintain the final position after animation
        animation.interpolator = BounceInterpolator() // Set the interpolator to simulate bouncing effect
        imageButton.startAnimation(animation) // Start the animation
    }

    private fun resetPosition(imageButton: ImageButton) {
        val animation = TranslateAnimation(0f, 0f, imageButton.translationY, 0f) // Define the animation
        animation.duration = 1000 // Set the duration of the animation
        animation.fillAfter = true // Maintain the final position after animation
        imageButton.startAnimation(animation) // Start the animation

        // Reset translationY back to 0
        imageButton.translationY = 0f
    }


    private fun startBackgroundAnimation() {
        // Set animation-list drawable as background
        findViewById<View>(android.R.id.content).setBackgroundResource(R.drawable.gradient_list)
        // Get AnimationDrawable reference
        animationDrawable = findViewById<View>(android.R.id.content).background as AnimationDrawable
        // Start animation
        animationDrawable.start()
    }

    private fun stopBackgroundAnimation() {
        // Stop animation
        animationDrawable.stop()
    }

    override fun onPause() {
        super.onPause()
        val imageButton = findViewById<ImageButton>(R.id.imageButton)
        imageButton.clearAnimation() // Cancel animation when navigating away from the ImageButton section
    }

    fun showPrevious(view: View) {
        if (currentPosition > 0) {
            onPause()
            controls[currentPosition].visibility = View.GONE
            currentPosition--
            controls[currentPosition].visibility = View.VISIBLE
            controlNameTextView.text = controlNames[currentPosition]
        }
    }

    fun showNext(view: View) {
        if (currentPosition < controls.size - 1) {
            onPause()
            controls[currentPosition].visibility = View.GONE
            currentPosition++
            controls[currentPosition].visibility = View.VISIBLE
            controlNameTextView.text = controlNames[currentPosition]
        }
    }
}
