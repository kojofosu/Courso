package com.edue.courso;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends BottomSheetDialogFragment {

    String bottom_code, bottom_title, bottom_dept, bottom_level, bottom_programme;
    TextView bottomSheetCode, bottomSheetTitle, bottomSHeetDept, bottomSheetLevel, bottomSheetProgramme;

    public DetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        bottomSheetCode = view.findViewById(R.id.bottom_sheet_code);
        bottomSheetTitle = view.findViewById(R.id.bottom_sheet_title);
        bottomSHeetDept = view.findViewById(R.id.bottom_sheet_dept);
        bottomSheetLevel = view.findViewById(R.id.bottom_sheet_level);
        bottomSheetProgramme = view.findViewById(R.id.bottom_sheet_programme);

        if (getArguments() != null) {
            bottom_code = getArguments().getString("bottomCode");
            bottom_dept = getArguments().getString("bottomDept");
            bottom_level = getArguments().getString("bottomLevel");
            bottom_programme = getArguments().getString("bottomProgramme");
            bottom_title = getArguments().getString("bottomTitle");

            bottomSheetCode.setText(bottom_code);
            bottomSheetTitle.setText(bottom_title);
            bottomSHeetDept.setText(bottom_dept);
            bottomSheetLevel.setText(String.format("Level %s", bottom_level));
            bottomSheetProgramme.setText(bottom_programme);
        }


        return view;
    }

}
