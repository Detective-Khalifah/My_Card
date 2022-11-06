package com.blogspot.thengnet.mycard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

import com.blogspot.thengnet.mycard.databinding.FragmentSocialBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class SocialFragment extends Fragment {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 30000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler(Looper.myLooper());
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run () {
            // Delayed removal of status and navigation bar
            if (Build.VERSION.SDK_INT >= 30) {
                mContentView.getWindowInsetsController().hide(
                        WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            } else {
                // Note that some of these constants are new as of API 16 (Jelly Bean)
                // and API 19 (KitKat). It is safe to use them, as they are inlined
                // at compile-time and do nothing on earlier devices.
                mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run () {
            // Delayed display of UI elements
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run () {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch (View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (AUTO_HIDE) {
                        delayedHide(AUTO_HIDE_DELAY_MILLIS);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    view.performClick();
                    break;
                default:
                    break;
            }
            return false;
        }
    };
    private FragmentSocialBinding binding;

    @Override
    public void onCreate (@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSocialBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mVisible = true;
        mControlsView = binding.fullscreenContentControls;
        mContentView = binding.scrollView;

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        binding.scrollView.setOnTouchListener(mDelayHideTouchListener);

        // Upon touching the button, open up BioFragment
        binding.buttonToBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                NavHostFragment.findNavController(SocialFragment.this).navigate(R.id.action_SocialFragment_to_BioFragment);
            }
        });

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);

        binding.buttonFacebook.setOnClickListener(new ProfileClickListener());
        binding.buttonInstagram.setOnClickListener(new ProfileClickListener());
        binding.buttonLinkedin.setOnClickListener(new ProfileClickListener());
        binding.buttonTwitter.setOnClickListener(new ProfileClickListener());
        binding.buttonGithub.setOnClickListener(new ProfileClickListener());
        binding.buttonWeb.setOnClickListener(new ProfileClickListener());
    }

    private Intent getBlogIntent () {
        return new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://thengnet.blogspot.com"));
    }

    private Intent getFacebookIntent () {
        try {
            getContext().getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/NGNet.crew"));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.facebook.com/Detective.Khalifah"));
        }
    }

    private Intent getGitHubIntent () {
        return new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://github.com/Detective-Khalifah"));
    }

    private Intent getInstagramIntent () {
        String url = "https://instagram.com/_u/detective_khalifah";
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        try {
            if (getContext().getPackageManager().getPackageInfo("com.instagram.android", 0) != null) {
                if (url.endsWith("/")) {
                    url = url.substring(0, url.length() - 1);
                }
                final String username = url.substring(url.lastIndexOf("/") + 1);
                // http://stackoverflow.com/questions/21505941/intent-to-open-instagram-user-profile-on-android
                intent.setData(Uri.parse("http://instagram.com/_u/" + "detective_khalifah"));
                intent.setPackage("com.instagram.android");
                return intent;
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        intent.setData(Uri.parse(url));
        return intent;
    }

    private Intent getLinkedInIntent () {
        String profile_url = "https://www.linkedin.com/in/Detective-Khalifah";
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(profile_url));
            intent.setPackage("com.linkedin.android");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            return intent;
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse(profile_url));
        }
    }

    private Intent getTwitterIntent () {
        try {
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("twitter://user?screen_name="
                            .concat("Detect_Khalifah")));

        } catch (Exception e) {
            return new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://twitter.com/#!/".concat("Detect_Khalifah")));
        }

    }

    private void toggle () {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide () {
        // Hide UI first
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void show () {
        // Show the system bar
        if (Build.VERSION.SDK_INT >= 30) {
            mContentView.getWindowInsetsController().show(
                    WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
        } else {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide (int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private class ProfileClickListener implements View.OnClickListener {

        @Override
        public void onClick (View view) {
            if (view.getId() == binding.buttonFacebook.getId())
                startActivity(getFacebookIntent());
            else if (view.getId() == binding.buttonInstagram.getId())
                startActivity(getInstagramIntent());
            else if (view.getId() == binding.buttonLinkedin.getId())
                startActivity(getLinkedInIntent());
            else if (view.getId() == binding.buttonTwitter.getId())
                startActivity(getTwitterIntent());
            else if (view.getId() == binding.buttonGithub.getId())
                startActivity(getGitHubIntent());
            else if (view.getId() == binding.buttonWeb.getId())
                startActivity(getBlogIntent());
        }
    }
}