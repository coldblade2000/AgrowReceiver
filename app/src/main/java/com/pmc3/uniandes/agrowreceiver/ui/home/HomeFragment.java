package com.pmc3.uniandes.agrowreceiver.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.pmc3.uniandes.agrowreceiver.MainViewModel;
import com.pmc3.uniandes.agrowreceiver.ServerThread;
import com.pmc3.uniandes.agrowreceiver.databinding.FragmentHomeBinding;

import java.io.IOException;
import java.net.ServerSocket;

public class HomeFragment extends Fragment {

    public static final String TAG = "HomeFragment";
    MainViewModel model;

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



        /*final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);*/
        return root;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        model = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        binding.switch1.setOnCheckedChangeListener((CompoundButton var1, boolean var2) -> {
            if (var2){
                try {
                    model.serverStart();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Start server failed!");
                    Toast.makeText(getContext(), "Start server failed!", Toast.LENGTH_SHORT).show();
                    model.repository.isServerOn.setValue(false);

                }
            }else{
                try {
                    model.serverStop();
                } catch (Exception e) {
                    Log.e(TAG, "Stop server failed!");
                    Toast.makeText(getContext(), "Stop server failed!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    model.repository.isServerOn.setValue(true);
                }
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}