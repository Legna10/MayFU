package com.example.mayfu11;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Home extends Fragment {

    private static final String PREFS_NAME = "user_data"; // Same preferences file
    private static final String KEY_WEIGHT = "weight"; // Key for weight
    private static final String KEY_HEIGHT = "height"; // Key for height
    private static final String KEY_GENDER = "gender"; // Key for gender
    private static final String KEY_AGE = "age"; //Key for AGE

    private TextView bmiTextView, bmiCategoryTextView, idealWeightTextView, caloriesTextView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the TextViews
        bmiTextView = view.findViewById(R.id.BMI);
        bmiCategoryTextView = view.findViewById(R.id.BMICategory);
        idealWeightTextView = view.findViewById(R.id.IdealWeight);
        caloriesTextView = view.findViewById(R.id.CaloriesNeeded);
        TextView nutritionalCarbsTextView = view.findViewById(R.id.Carbs);
        TextView nutritionalProteinTextView = view.findViewById(R.id.Protein);
        TextView nutritionalFatTextView = view.findViewById(R.id.Fat);

        // Load saved data from SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Retrieve user data (weight, height, gender)
        String weight = sharedPreferences.getString(KEY_WEIGHT, "");
        String height = sharedPreferences.getString(KEY_HEIGHT, "");
        int gender = sharedPreferences.getInt(KEY_GENDER, -1); // -1 as default value if not set
        String age = sharedPreferences.getString(KEY_AGE, "");

        // Ensure data is available for calculations
        if (!TextUtils.isEmpty(weight) && !TextUtils.isEmpty(height) && !TextUtils.isEmpty(age)) {
            try {
                // Parse weight and height to float values
                float weightValue = Float.parseFloat(weight);
                float heightValue = Float.parseFloat(height) / 100; // Convert height to meters
                float ageValue = Float.parseFloat(age);

                // Calculate BMI
                float bmi = weightValue / (heightValue * heightValue);
                bmiTextView.setText(String.format("Your BMI is %.2f", bmi));

                // Determine BMI category
                String bmiCategory = getBmiCategory(bmi);
                bmiCategoryTextView.setText("Category: " + bmiCategory);

                // Calculate ideal weight based on height (using the BMI range 18.5 to 24.9)
                float idealWeightMin = 18.5f * (heightValue * heightValue);
                float idealWeightMax = 24.9f * (heightValue * heightValue);
                idealWeightTextView.setText(String.format("Ideal weight: %.1f - %.1f kg", idealWeightMin, idealWeightMax));

                // Calculate calories needed (Harris-Benedict equation)
                float bmr = calculateBmr(weightValue, heightValue, gender, ageValue);
                float calories = calculateCalories(bmr);
                caloriesTextView.setText(String.format("Calories Needed: %.0f kcal/day", calories));

                // Nutritional Information
                float carbs = calories * 0.45f / 4;  // 45% of calories from carbs, 1g carbs = 4 kcal
                float protein = calories * 0.30f / 4; // 30% of calories from protein, 1g protein = 4 kcal
                float fat = calories * 0.25f / 9;     // 25% of calories from fat, 1g fat = 9 kcal

                // Update nutritional values
                nutritionalCarbsTextView.setText(String.format("%.1f g", carbs));
                nutritionalProteinTextView.setText(String.format(" %.1f g", protein));
                nutritionalFatTextView.setText(String.format(" %.1f g", fat));

            } catch (NumberFormatException e) {
                // Handle invalid number format (in case the values are non-numeric)
                bmiTextView.setText("BMI not available");
                bmiCategoryTextView.setText("Category: N/A");
                idealWeightTextView.setText("Ideal weight: N/A");
                caloriesTextView.setText("Calories Needed: N/A");
                nutritionalCarbsTextView.setText("Carbs: 0 g");
                nutritionalProteinTextView.setText("Protein: 0 g");
                nutritionalFatTextView.setText("Fat: 0 g");

                // Log the error for debugging
                Log.e("Home", "Error parsing user data: " + e.getMessage());
            }
        } else {
            // If data is missing, set default values
            bmiTextView.setText("BMI not available");
            bmiCategoryTextView.setText("Category: N/A");
            idealWeightTextView.setText("Ideal weight: N/A");
            caloriesTextView.setText("Calories Needed: N/A");
            nutritionalCarbsTextView.setText("Carbs: 0 g");
            nutritionalProteinTextView.setText("Protein: 0 g");
            nutritionalFatTextView.setText("Fat: 0 g");

            // Log the missing data
            Log.w("Home", "User data is incomplete");
        }
    }


    // Method to determine BMI category
    private String getBmiCategory(float bmi) {
        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi >= 18.5 && bmi < 24.9) {
            return "Normal weight";
        } else if (bmi >= 25 && bmi < 29.9) {
            return "Overweight";
        } else {
            return "Obesity";
        }
    }

    // Method to calculate BMR (Harris-Benedict equation)
    private float calculateBmr(float weight, float height, int gender, float age) {
        if (gender == 0) { // Male
            return 66.5f + (13.75f * weight) + (5.003f * height * 100) - (6.75f * age);
        } else { // Female
            return 655f + (9.563f * weight) + (1.850f * height * 100) - (4.676f * age);
        }
    }

    // Method to calculate calories needed
    private float calculateCalories(float bmr) {
        return bmr * 1.375f; // Assuming lightly active
    }
}
