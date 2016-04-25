package com.adkdevelopment.simpleflashlightadfree;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by karataev on 2/22/16.
 */
public class MorseFragment extends android.support.v4.app.Fragment {

    private int status;
    private String morseCode, beforeChangeMorse;

    @Bind(R.id.button_image) ImageView mButtonImage;
    @Bind(R.id.flashlight_mode) TextView mStatusText;
    @Bind(R.id.morse_current_text) TextView mCurrentText;
    @Bind(R.id.edittext_morse) EditText mMorseInput;

    public MorseFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            status = savedInstanceState.getInt(FlashlightService.STATUS);

            if (status != 0) {
                Intent intent = new Intent(getActivity().getApplication(), FlashlightService.class);
                intent.putExtra(FlashlightService.STATUS, status);

                // add morse code
                intent.putExtra(FlashlightService.MORSE, morseCode);

                getActivity().getApplication().startService(intent);
            }
        } else {
            status = 0;
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_morse, container, false);

        ButterKnife.bind(this, rootView);

        // check status and use correct image
        Utility.setSwitchColor(mStatusText, mButtonImage, status);

        mButtonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start service on click
                if (status == FlashlightService.STATUS_OFF) {
                    status = FlashlightService.STATUS_MORSE;
                } else {
                    status = FlashlightService.STATUS_OFF;
                }

                // Set button drawable
                Utility.setSwitchColor(mStatusText, mButtonImage, status);

                startService();
            }
        });

        mMorseInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeChangeMorse = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                morseCode = s.toString();
                mCurrentText.setText(Utility.getMorseMessage(morseCode));

                if (s.length() > 0 || beforeChangeMorse.length() > 0) {
                    startService();
                }
            }
        });


        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Stop service on application exit
        Intent intent = new Intent(getActivity().getApplication(), FlashlightService.class);
        getActivity().getApplication().stopService(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save status on rotate, possibly will remove rotation in the future
        outState.putInt(FlashlightService.STATUS, status);
    }

    private void startService() {
        Intent intent = new Intent(getActivity().getApplication(), FlashlightService.class);

        intent.putExtra(FlashlightService.STATUS, status);

        // add morse code
        intent.putExtra(FlashlightService.MORSE, morseCode);

        getActivity().getApplication().startService(intent);
    }

}
