package in.engineerakash.covid19india.ui.precaution;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import in.engineerakash.covid19india.databinding.FragmentPrecautionBinding;

public class PrecautionFragment extends Fragment {

    private FragmentPrecautionBinding binding;

    public PrecautionFragment() {
        // Required empty public constructor
    }

    public static PrecautionFragment newInstance() {
        PrecautionFragment fragment = new PrecautionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentPrecautionBinding.inflate(inflater, container, false);

        initComponent();

        return binding.getRoot();
    }

    private void initComponent() {
        if (((AppCompatActivity) getActivity()) != null && ((AppCompatActivity) getActivity()).getSupportActionBar() != null)
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

    }
}
