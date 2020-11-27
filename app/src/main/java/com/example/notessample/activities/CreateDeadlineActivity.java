package com.example.notessample.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.notessample.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class CreateDeadlineActivity extends AppCompatActivity {

    private EditText inputDeadlineTitle, inputDeadlineSubtitle, inputDeadlineText;
    private TextView textDateTimeDeadline;
    private View viewSubtitleIndicatorDeadline;

    private String selectedDeadlineColor;

    private AlertDialog dialogDeleteDeadline;

    private com.example.notessample.entities.Deadline alreadyAvailableDeadline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_deadlines);

        ImageView imageBack = findViewById(R.id.imageBackDeadline);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        inputDeadlineTitle = findViewById(R.id.inputDeadlineTitle);
        inputDeadlineSubtitle = findViewById(R.id.inputDeadlineSubtitle);
        inputDeadlineText = findViewById(R.id.inputDeadline);
        textDateTimeDeadline = findViewById(R.id.textDateTimeDeadline);
        viewSubtitleIndicatorDeadline = findViewById(R.id.viewSubtitleIndicatorDeadline);
        textDateTimeDeadline.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                        .format(new Date())
        );

        ImageView imageSave = findViewById(R.id.imageSaveDeadline);
        imageSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void  onClick(View v) {
                saveDeadline();
            }
        });

        selectedDeadlineColor = "#333333";

        if(getIntent().getBooleanExtra("isViewOrUpdate", false)) {
            alreadyAvailableDeadline = (com.example.notessample.entities.Deadline) getIntent().getSerializableExtra("deadline");
            setViewOrUpdateDeadline();
        }

        initMiscellaneousDeadline();
        setSubtitleIndicatorColorDeadline();

    }

    private void setViewOrUpdateDeadline() {
        inputDeadlineTitle.setText(alreadyAvailableDeadline.getTitle());
        inputDeadlineSubtitle.setText(alreadyAvailableDeadline.getSubtitle());
        inputDeadlineText.setText(alreadyAvailableDeadline.getDeadlineText());
        textDateTimeDeadline.setText(alreadyAvailableDeadline.getDateTime());
        /*
            if(alreadyAvailableNote.getWebLink() != null && !alreadyAvailableNote.getWebLink().trim().isEmpty()) {
            textWebURL.setText(alreadyAvailableNote.getWebLink());
            layoutWebURL.setVisibility(View.VISIBLE);
        }
         */
    }

    private void saveDeadline() {
        if (inputDeadlineTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Title can not be empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (inputDeadlineSubtitle.getText().toString().trim().isEmpty() &&
                inputDeadlineText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Deadline can not be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        final com.example.notessample.entities.Deadline deadline = new com.example.notessample.entities.Deadline();
        deadline.setTitle(inputDeadlineTitle.getText().toString());
        deadline.setSubtitle(inputDeadlineSubtitle.getText().toString());
        deadline.setDeadlineText(inputDeadlineText.getText().toString());
        deadline.setDateTime(textDateTimeDeadline.getText().toString());
        deadline.setColor(selectedDeadlineColor);

        if(alreadyAvailableDeadline != null) {
            deadline.setId(alreadyAvailableDeadline.getId()); //Making the ID same would call in replace from dao
        }

        @SuppressLint("StaticFieldLeak")
        class SaveDeadlineTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected  Void doInBackground(Void... voids) {
                com.example.notessample.database.DeadlinesDatabase.getDatabase(getApplicationContext()).deadlineDao().insertDeadline(deadline);
                return null;
            }

            @Override
            protected  void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }

        }

        new SaveDeadlineTask().execute();
    }

    private void initMiscellaneousDeadline() {
        final LinearLayout layoutMiscellaneous = findViewById(R.id.miscDeadlines);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous);
        layoutMiscellaneous.findViewById(R.id.textMiscDeadlines).setOnClickListener(new View.OnClickListener() {
            @Override
            public  void  onClick(View v) {
                if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else
                {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        final ImageView imageColor1 = layoutMiscellaneous.findViewById(R.id.imageColor1Deadlines);
        final ImageView imageColor2 = layoutMiscellaneous.findViewById(R.id.imageColor2Deadlines);
        final ImageView imageColor3 = layoutMiscellaneous.findViewById(R.id.imageColor3Deadlines);
        final ImageView imageColor4 = layoutMiscellaneous.findViewById(R.id.imageColor4Deadlines);
        final ImageView imageColor5 = layoutMiscellaneous.findViewById(R.id.imageColor5Deadlines);

        layoutMiscellaneous.findViewById(R.id.viewColor1Deadlines).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDeadlineColor = "#333333";
                imageColor1.setImageResource(R.drawable.ic_done);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColorDeadline();
            }
        });

        layoutMiscellaneous.findViewById(R.id.viewColor2Deadlines).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDeadlineColor = "#fdbe3b";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(R.drawable.ic_done);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColorDeadline();
            }
        });

        layoutMiscellaneous.findViewById(R.id.viewColor3Deadlines).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDeadlineColor = "#ff4842";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(R.drawable.ic_done);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColorDeadline();
            }
        });

        layoutMiscellaneous.findViewById(R.id.viewColor4Deadlines).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDeadlineColor = "#3a52fc";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(R.drawable.ic_done);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColorDeadline();
            }
        });

        layoutMiscellaneous.findViewById(R.id.viewColor5Deadlines).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDeadlineColor = "#000000";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(R.drawable.ic_done);
                setSubtitleIndicatorColorDeadline();
            }
        });

        if (alreadyAvailableDeadline != null && alreadyAvailableDeadline.getColor() != null && !alreadyAvailableDeadline.getColor().trim().isEmpty()) {
            switch (alreadyAvailableDeadline.getColor()){
                case "#fdbe3b":
                    layoutMiscellaneous.findViewById(R.id.viewColor2Deadlines).performClick();
                    break;
                case "#ff4842":
                    layoutMiscellaneous.findViewById(R.id.viewColor3Deadlines).performClick();
                    break;
                case "##3a52fc":
                    layoutMiscellaneous.findViewById(R.id.viewColor4Deadlines).performClick();
                    break;
                case "#000000":
                    layoutMiscellaneous.findViewById(R.id.viewColor5Deadlines).performClick();
                    break;
            }
        }


        if(alreadyAvailableDeadline != null) {
            layoutMiscellaneous.findViewById(R.id.layoutDeleteDeadlines).setVisibility(View.VISIBLE);
            layoutMiscellaneous.findViewById(R.id.layoutDeleteDeadlines).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    showDeleteDeadlineDialog();
                }
            });
        }

    }

    private void showDeleteDeadlineDialog() {
        if(dialogDeleteDeadline == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateDeadlineActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_delete_deadlines,
                    (ViewGroup) findViewById((R.id.layoutDeleteDeadlinesContainer))
            );
            builder.setView(view);
            dialogDeleteDeadline = builder.create();
            if(dialogDeleteDeadline.getWindow() != null) {
                dialogDeleteDeadline.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.textDeleteDeadlines).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    @SuppressLint("StaticFieldLeak")
                    class DeleteDeadlineTask extends AsyncTask<Void, Void, Void> {

                        @Override
                        protected Void doInBackground(Void... voids) {
                            com.example.notessample.database.DeadlinesDatabase.getDatabase(getApplicationContext()).deadlineDao()
                                    .deleteDeadline(alreadyAvailableDeadline);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            Intent intent = new Intent();
                            intent.putExtra("isDeadlineDeleted", true);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }

                    new DeleteDeadlineTask().execute();
                }

            });

            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogDeleteDeadline.dismiss();
                }
            });

        }

        dialogDeleteDeadline.show();
    }

    private void setSubtitleIndicatorColorDeadline() {
        GradientDrawable gradientDrawable = (GradientDrawable) viewSubtitleIndicatorDeadline.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedDeadlineColor));
    }

}