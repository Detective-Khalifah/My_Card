package com.blogspot.thengnet.mycard

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.view.View.OnTouchListener
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.blogspot.thengnet.mycard.databinding.FragmentSocialBinding

/**
 * A simple [Fragment] subclass.
 */
class SocialFragment : Fragment() {
    private val mHideHandler = Handler(Looper.myLooper()!!)
    private var mContentView: View? = null
    private val mHidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar
        if (Build.VERSION.SDK_INT >= 30) {
            mContentView!!.windowInsetsController!!.hide(
                WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars()
            )
        } else {
            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView!!.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        }
    }
    private var mControlsView: View? = null
    private val mShowPart2Runnable = Runnable { // Delayed display of UI elements
        val actionBar = (activity as AppCompatActivity?)!!.supportActionBar
        actionBar?.show()
        mControlsView!!.visibility = View.VISIBLE
    }
    private var mVisible = false
    private val mHideRunnable = Runnable { hide() }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val mDelayHideTouchListener = OnTouchListener { view, motionEvent ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS)
            }
            MotionEvent.ACTION_UP -> view.performClick()
            else -> {}
        }
        false
    }
    private var binding: FragmentSocialBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSocialBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mVisible = true
        mControlsView = binding!!.fullscreenContentControls
        mContentView = binding!!.scrollView

        // Set up the user interaction to manually show or hide the system UI.
        (mContentView as ScrollView).setOnClickListener(View.OnClickListener { toggle() })

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        binding!!.scrollView.setOnTouchListener(mDelayHideTouchListener)

        // Upon touching the button, open up BioFragment
        binding!!.buttonToBio.setOnClickListener {
            NavHostFragment.findNavController(this@SocialFragment)
                .navigate(R.id.action_SocialFragment_to_BioFragment)
        }

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)
        binding!!.buttonFacebook.setOnClickListener(ProfileClickListener())
        binding!!.buttonInstagram.setOnClickListener(ProfileClickListener())
        binding!!.buttonLinkedin.setOnClickListener(ProfileClickListener())
        binding!!.buttonTwitter.setOnClickListener(ProfileClickListener())
        binding!!.buttonGithub.setOnClickListener(ProfileClickListener())
        binding!!.buttonWeb.setOnClickListener(ProfileClickListener())
    }

    private val blogIntent: Intent
        private get() = Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://thengnet.blogspot.com"))
    private val facebookIntent: Intent
        private get() = try {
            requireContext().packageManager.getPackageInfo("com.facebook.katana", 0)
            Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/NGNet.crew"))
        } catch (e: Exception) {
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.facebook.com/Detective.Khalifah")
            )
        }
    private val gitHubIntent: Intent
        private get() = Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://github.com/Detective-Khalifah"))

    // http://stackoverflow.com/questions/21505941/intent-to-open-instagram-user-profile-on-android
    private val instagramIntent: Intent
        private get() {
            var url = "https://instagram.com/_u/detective_khalifah"
            val intent = Intent(Intent.ACTION_VIEW)
            try {
                if (requireContext().packageManager.getPackageInfo(
                        "com.instagram.android",
                        0
                    ) != null
                ) {
                    if (url.endsWith("/")) {
                        url = url.substring(0, url.length - 1)
                    }
                    val username = url.substring(url.lastIndexOf("/") + 1)
                    // http://stackoverflow.com/questions/21505941/intent-to-open-instagram-user-profile-on-android
                    intent.data = Uri.parse("http://instagram.com/_u/" + "detective_khalifah")
                    intent.setPackage("com.instagram.android")
                    return intent
                }
            } catch (ignored: PackageManager.NameNotFoundException) {
            }
            intent.data = Uri.parse(url)
            return intent
        }
    private val linkedInIntent: Intent
        private get() {
            val profile_url = "https://www.linkedin.com/in/Detective-Khalifah"
            return try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(profile_url))
                intent.setPackage("com.linkedin.android")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent
            } catch (e: Exception) {
                Intent(Intent.ACTION_VIEW, Uri.parse(profile_url))
            }
        }
    private val twitterIntent: Intent
        private get() = try {
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(
                    "twitter://user?screen_name="
                            + "Detect_Khalifah"
                )
            )
        } catch (e: Exception) {
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://twitter.com/#!/" + "Detect_Khalifah")
            )
        }

    private fun toggle() {
        if (mVisible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        val actionBar = (activity as AppCompatActivity?)!!.supportActionBar
        actionBar?.hide()
        mControlsView!!.visibility = View.GONE
        mVisible = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        // Show the system bar
        if (Build.VERSION.SDK_INT >= 30) {
            mContentView!!.windowInsetsController!!.show(
                WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars()
            )
        } else {
            mContentView!!.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        }
        mVisible = true

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    private inner class ProfileClickListener : View.OnClickListener {
        override fun onClick(view: View) {
            if (view.id == binding!!.buttonFacebook.id) startActivity(facebookIntent) else if (view.id == binding!!.buttonInstagram.id) startActivity(
                instagramIntent
            ) else if (view.id == binding!!.buttonLinkedin.id) startActivity(linkedInIntent) else if (view.id == binding!!.buttonTwitter.id) startActivity(
                twitterIntent
            ) else if (view.id == binding!!.buttonGithub.id) startActivity(
                gitHubIntent
            ) else if (view.id == binding!!.buttonWeb.id) startActivity(blogIntent)
        }
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [.AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = true

        /**
         * If [.AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private const val AUTO_HIDE_DELAY_MILLIS = 30000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private const val UI_ANIMATION_DELAY = 300
    }
}