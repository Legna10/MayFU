package com.example.mayfu11;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class Profile extends Fragment {

    private static final String PREFS_NAME = "user_data";
    private static final String KEY_WEIGHT = "weight";
    private static final String KEY_HEIGHT = "height";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_AGE = "age";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get references to views
        TextInputLayout weightInputLayout = view.findViewById(R.id.weightInputLayout);
        EditText weightEditText = view.findViewById(R.id.etWeight);
        TextInputLayout heightInputLayout = view.findViewById(R.id.heightInputLayout);
        EditText heightEditText = view.findViewById(R.id.etHeight);
        TextInputLayout ageInputLayout = view.findViewById(R.id.ageInputLayout);
        EditText ageEditText = view.findViewById(R.id.etAge);
        Spinner genderSpinner = view.findViewById(R.id.spinnerGender);
        MaterialButton submitButton = view.findViewById(R.id.btnSubmit);

        // Set up Spinner for gender selection
        String[] genderOptions = getResources().getStringArray(R.array.gender_options);
        String[] optionsWithPlaceholder = new String[genderOptions.length + 1];
        optionsWithPlaceholder[0] = "Select Gender";  // Placeholder for the spinner
        System.arraycopy(genderOptions, 0, optionsWithPlaceholder, 1, genderOptions.length);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, optionsWithPlaceholder);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

        // Load saved data from SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Load saved weight, height, and gender from SharedPreferences
        String savedWeight = sharedPreferences.getString(KEY_WEIGHT, "");
        String savedHeight = sharedPreferences.getString(KEY_HEIGHT, "");
        int savedGenderPosition = sharedPreferences.getInt(KEY_GENDER, 0);
        String savedAge = sharedPreferences.getString(KEY_AGE, "");

        // Set the saved data to the views
        weightEditText.setText(savedWeight);
        heightEditText.setText(savedHeight);
        ageEditText.setText(savedAge);

        // Ensure the savedGenderPosition is within the bounds of the optionsWithPlaceholder array
        if (savedGenderPosition >= 0 && savedGenderPosition < optionsWithPlaceholder.length) {
            genderSpinner.setSelection(savedGenderPosition); // Set the spinner to the saved position
        } else {
            genderSpinner.setSelection(0);  // Default to "Select Gender" if position is invalid
        }

        // Set the hint visibility based on text presence for weight input
        updateHint(weightEditText, weightInputLayout, "Enter Weight", "Weight");
        updateHint(heightEditText, heightInputLayout, "Enter Height", "Height");
        updateHint(ageEditText, ageInputLayout, "Enter Age", "Age");

        // Add TextWatcher to dynamically change the hint for weight input
        weightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                updateHint(weightEditText, weightInputLayout, "Enter Weight", "Weight");
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Add TextWatcher to dynamically change the hint for height input
        heightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                updateHint(heightEditText, heightInputLayout, "Enter Height", "Height");
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Add TextWatcher to dynamically change the hint for age input
        ageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                updateHint(ageEditText, ageInputLayout, "Enter Age", "Age");
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Handle Submit button click
        submitButton.setOnClickListener(v -> {
            // Get inputs
            String weightInput = weightEditText.getText().toString().trim();
            String heightInput = heightEditText.getText().toString().trim();
            int selectedPosition = genderSpinner.getSelectedItemPosition();
            String ageInput = ageEditText.getText().toString().trim();

            boolean isValid = true;

            // Validate Weight
            if (TextUtils.isEmpty(weightInput)) {
                weightEditText.setError("Please enter your weight");
                isValid = false;
            }

            // Validate Height
            if (TextUtils.isEmpty(heightInput)) {
                heightEditText.setError("Please enter your height");
                isValid = false;
            }

            // Validate Gender
            if (selectedPosition == 0) {
                TextView errorView = (TextView) genderSpinner.getSelectedView();
                if (errorView != null) {
                    errorView.setTextColor(Color.RED);
                    errorView.setText("Select Gender!");
                }
                isValid = false;
            }

            // Validate Age
            if (TextUtils.isEmpty(ageInput) || Integer.parseInt(ageInput) < 1 || Integer.parseInt(ageInput) > 100) {
                ageEditText.setError("Please enter a valid age");
                isValid = false;
            }

            // If all inputs are valid, save data
            if (isValid) {
                // Save to SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(KEY_WEIGHT, weightInput);
                editor.putString(KEY_HEIGHT, heightInput);
                editor.putInt(KEY_GENDER, selectedPosition);
                editor.putString(KEY_AGE, ageInput);
                editor.apply();  // Apply changes asynchronously

                // Log the saved data to check if it's correctly stored
                Log.d("Profile", "Data Saved - Weight: " + weightInput + ", Height: " + heightInput + ", Gender Position: " + selectedPosition + ", Age: " + ageInput);

                Toast.makeText(requireContext(), "Data submitted successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle Done action on ageEditText (hide keyboard, remove focus, hide cursor)
        ageEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Hide keyboard
                hideKeyboard(v);

                // Remove focus from the EditText
                v.clearFocus();

                // Hide the cursor (no more blinking line)
                v.setCursorVisible(false);

                return true;  // Indicate that we handled the action
            }
            return false;
        });
    }

    // Helper method to hide keyboard
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // Helper method to update hint visibility
    private void updateHint(EditText editText, TextInputLayout inputLayout, String emptyHint, String activeHint) {
        if (TextUtils.isEmpty(editText.getText())) {
            inputLayout.setHint(emptyHint);  // Show the default hint
        } else {
            inputLayout.setHint(activeHint);  // Show the active hint
        }
    }
}
