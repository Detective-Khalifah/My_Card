package com.blogspot.thengnet.mycard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blogspot.thengnet.mycard.databinding.FragmentBioBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class BioFragment extends Fragment {

    private FragmentBioBinding binding;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Upon touching the button, open up BioFragment
        binding.buttonToSocial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                NavHostFragment.findNavController(BioFragment.this).navigate(R.id.action_BioFragment_to_SocialFragment);
            }
        });
    }
}