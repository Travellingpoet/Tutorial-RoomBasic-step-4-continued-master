package com.example.roombasic;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class WordAddFragment extends Fragment {
    private Button buttonSubmit;
    private EditText editTextEnglish,editTextChinese;
    WordViewModel wordViewModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_word,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final FragmentActivity activity = requireActivity();
        wordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
        buttonSubmit = activity.findViewById(R.id.button);
        editTextEnglish = activity.findViewById(R.id.editTextTextPersonName);
        editTextChinese = activity.findViewById(R.id.editTextTextPersonName2);
        buttonSubmit.setEnabled(false);
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editTextEnglish,0);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String english = editTextEnglish.getText().toString().trim();
                String chinese = editTextChinese.getText().toString().trim();
                buttonSubmit.setEnabled(!english.isEmpty() && !chinese.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        editTextChinese.addTextChangedListener(textWatcher);
        editTextEnglish.addTextChangedListener(textWatcher);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String english = editTextEnglish.getText().toString().trim();
                String chinese = editTextChinese.getText().toString().trim();
                Word word = new Word(english,chinese);
                wordViewModel.insertWords(word);
                NavController navController = Navigation.findNavController(view);
                navController.navigateUp();
                InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(),0);
            }
        });
    }

}
