package com.pmc3.uniandes.agrowreceiver.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.ViewModelProvider;

import com.pmc3.uniandes.agrowreceiver.ServerThread;
import com.pmc3.uniandes.agrowreceiver.databinding.FragmentHomeBinding;

import java.net.ServerSocket;

public class HomeFragment extends Fragment {

    public static final int SERVER_PORT = 8234;

    private FragmentHomeBinding binding;
    private ServerSocket socket;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    public void serverStart(){
        ServerThread serverThread = new ServerThread(socket, SERVER_PORT, this);
        serverThread.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}