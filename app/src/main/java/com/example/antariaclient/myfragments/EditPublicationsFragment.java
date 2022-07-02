package com.example.antariaclient.myfragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.antariaclient.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditPublicationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditPublicationsFragment extends Fragment {

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EditPublicationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment EditPublicationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditPublicationsFragment newInstance() {
        EditPublicationsFragment fragment = new EditPublicationsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_publications, container, false);
    }
}