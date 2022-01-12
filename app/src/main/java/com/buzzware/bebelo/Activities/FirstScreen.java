package com.buzzware.bebelo.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.buzzware.bebelo.R;
import com.buzzware.bebelo.classes.SessionManager;
import com.buzzware.bebelo.databinding.ActivityFirstScreenBinding;

import java.util.Calendar;

public class FirstScreen extends AppCompatActivity {

    ActivityFirstScreenBinding binding;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFirstScreenBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        Init();



    }

    private void Init() {

        context = this;

        if(SessionManager.getInstance().getFilter(FirstScreen.this)==null){
            SessionManager.getInstance().setFilter(FirstScreen.this,"No");
        }

        SetListeners();

    }

    private void SetListeners() {

        binding.btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isValid()) {

                    startActivity(new Intent(FirstScreen.this, Home.class));

                } else {
                    Toast.makeText(FirstScreen.this, "Date of birth and gender is not selected please provide date of birth and select gender or your age must be 18+" , Toast.LENGTH_LONG).show();

                }

            }
        });

        binding.btnMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetGenderView(0);
            }
        });

        binding.btnFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetGenderView(1);
            }
        });

        binding.dateET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (binding.dateET.getText().toString().length() == 2) {

                    int date;

                    try {

                        date = Integer.parseInt(binding.dateET.getText().toString());

                        if (date <= 31 && date > 0) {

                            binding.monthET.requestFocus();

                        } else {

                            binding.dateET.setText("");

                            Toast.makeText(FirstScreen.this, "Please enter a valid Date!", Toast.LENGTH_SHORT).show();

                        }
                    } catch (Exception e) {

                        binding.dateET.setText("");

                        Toast.makeText(FirstScreen.this, "Please enter a valid Date!", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });

        binding.monthET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (binding.monthET.getText().toString().length() == 2) {

                    int date;

                    try {

                        date = Integer.parseInt(binding.monthET.getText().toString());

                        if (date <= 12 && date > 0) {

                            binding.yearET.requestFocus();

                        } else {

                            binding.monthET.setText("");

                            Toast.makeText(FirstScreen.this, "Please enter a valid Month!", Toast.LENGTH_SHORT).show();

                        }

                    } catch (Exception e) {

                        binding.monthET.setText("");

                        Toast.makeText(FirstScreen.this, "Please enter a valid Month!", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });

        binding.yearET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (binding.yearET.getText().toString().length() == 4) {

                    int date;

                    try {

                        date = Integer.parseInt(binding.yearET.getText().toString());

                        if (getYear(date)) {

                            hideSoftKeyboard(FirstScreen.this);

                            checkUserAge();

                        } else {

                            binding.yearET.setText("");

                            Toast.makeText(FirstScreen.this, "Please enter a valid Year!", Toast.LENGTH_SHORT).show();

                        }
                    } catch (Exception e) {

                        binding.yearET.setText("");

                        Toast.makeText(FirstScreen.this, "Please enter a valid Year!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private boolean isValid() {

        if (binding.dateET.getText().toString().isEmpty()) {

            return false;

        } else if (binding.monthET.getText().toString().isEmpty()) {

            return false;

        } else if (binding.yearET.getText().toString().isEmpty()) {

            return false;

        } else if (binding.yearET.getText().toString().length() < 4) {

            return false;

        }

        return true;

    }

    private void checkUserAge() {

        int year = Integer.parseInt(binding.yearET.getText().toString());

        int month = Integer.parseInt(binding.monthET.getText().toString());

        int date = Integer.parseInt(binding.dateET.getText().toString());

        if (getAge(year, month, date) < 18) {

            binding.dateET.setText("");

            binding.monthET.setText("");

            binding.yearET.setText("");

            Toast.makeText(FirstScreen.this, "You must be 18+", Toast.LENGTH_SHORT).show();

        }
    }

    private boolean getYear(int year) {
        Calendar dob = Calendar.getInstance();

        int y = dob.get(Calendar.YEAR);

        if (year > y) {

            return false;

        } else {

            return true;

        }
    }

    private int getAge(int year, int month, int day) {

        Calendar dob = Calendar.getInstance();

        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        Integer ageInt = new Integer(age);


        return ageInt;
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);

        if (inputMethodManager.isAcceptingText()) {

            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }

    public void SetGenderView(int position) {

        if (position == 0) {

            binding.btnMale.setBackground(getResources().getDrawable(R.drawable.rounder_border_black_light));

            binding.btnFemale.setBackground(getResources().getDrawable(R.drawable.rounder_border_gray_light));

        } else if (position == 1) {

            binding.btnMale.setBackground(getResources().getDrawable(R.drawable.rounder_border_gray_light));

            binding.btnFemale.setBackground(getResources().getDrawable(R.drawable.rounder_border_black_light));

        }
    }
}