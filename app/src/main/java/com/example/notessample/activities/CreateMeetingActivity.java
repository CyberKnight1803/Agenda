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
import com.example.notessample.database.MeetingsDatabase;
import com.example.notessample.entities.Meeting;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateMeetingActivity extends AppCompatActivity {

    private EditText inputMeetingTitle, inputMeetingSubtitle, inputMeetingText;
    private TextView textDateTimeMeeting;
    private View viewSubtitleIndicatorMeeting;

    private String selectedMeetingColor;

    private AlertDialog dialogDeleteMeeting;

    private Meeting alreadyAvailableMeeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meetings);

        ImageView imageBack = findViewById(R.id.imageBackMeeting);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        inputMeetingTitle = findViewById(R.id.inputMeetingTitle);
        inputMeetingSubtitle = findViewById(R.id.inputMeetingSubtitle);
        inputMeetingText = findViewById(R.id.inputMeeting);
        textDateTimeMeeting = findViewById(R.id.textDateTimeMeeting);
        viewSubtitleIndicatorMeeting = findViewById(R.id.viewSubtitleIndicatorMeeting);
        textDateTimeMeeting.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                        .format(new Date())
        );

        ImageView imageSave = findViewById(R.id.imageSaveMeeting);
        imageSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void  onClick(View v) {
                saveMeeting();
            }
        });

        selectedMeetingColor = "#333333";

        if(getIntent().getBooleanExtra("isViewOrUpdate", false)) {
            alreadyAvailableMeeting = (Meeting) getIntent().getSerializableExtra("meeting");
            setViewOrUpdateMeeting();
        }

        initMiscellaneousMeeting();
        setSubtitleIndicatorColorMeeting();

    }

    private void setViewOrUpdateMeeting() {
        inputMeetingTitle.setText(alreadyAvailableMeeting.getTitle());
        inputMeetingSubtitle.setText(alreadyAvailableMeeting.getSubtitle());
        inputMeetingText.setText(alreadyAvailableMeeting.getMeetingText());
        textDateTimeMeeting.setText(alreadyAvailableMeeting.getDateTime());
        /*
            if(alreadyAvailableNote.getWebLink() != null && !alreadyAvailableNote.getWebLink().trim().isEmpty()) {
            textWebURL.setText(alreadyAvailableNote.getWebLink());
            layoutWebURL.setVisibility(View.VISIBLE);
        }
         */
    }

    private void saveMeeting() {
        if (inputMeetingTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Title can not be empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (inputMeetingSubtitle.getText().toString().trim().isEmpty() &&
                inputMeetingText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Meeting can not be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        final Meeting meeting = new Meeting();
        meeting.setTitle(inputMeetingTitle.getText().toString());
        meeting.setSubtitle(inputMeetingSubtitle.getText().toString());
        meeting.setMeetingText(inputMeetingText.getText().toString());
        meeting.setDateTime(textDateTimeMeeting.getText().toString());
        meeting.setColor(selectedMeetingColor);

        if(alreadyAvailableMeeting != null) {
            meeting.setId(alreadyAvailableMeeting.getId()); //Making the ID same would call in replace from dao
        }

        @SuppressLint("StaticFieldLeak")
        class SaveMeetingTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected  Void doInBackground(Void... voids) {
                MeetingsDatabase.getDatabase(getApplicationContext()).meetingDao().insertMeeting(meeting);
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

        new SaveMeetingTask().execute();
    }

    private void initMiscellaneousMeeting() {
        final LinearLayout layoutMiscellaneous = findViewById(R.id.miscMeetings);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous);
        layoutMiscellaneous.findViewById(R.id.textMiscMeetings).setOnClickListener(new View.OnClickListener() {
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

        final ImageView imageColor1 = layoutMiscellaneous.findViewById(R.id.imageColor1Meetings);
        final ImageView imageColor2 = layoutMiscellaneous.findViewById(R.id.imageColor2Meetings);
        final ImageView imageColor3 = layoutMiscellaneous.findViewById(R.id.imageColor3Meetings);
        final ImageView imageColor4 = layoutMiscellaneous.findViewById(R.id.imageColor4Meetings);
        final ImageView imageColor5 = layoutMiscellaneous.findViewById(R.id.imageColor5Meetings);

        layoutMiscellaneous.findViewById(R.id.viewColor1Meetings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedMeetingColor = "#333333";
                imageColor1.setImageResource(R.drawable.ic_done);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColorMeeting();
            }
        });

        layoutMiscellaneous.findViewById(R.id.viewColor2Meetings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedMeetingColor = "#fdbe3b";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(R.drawable.ic_done);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColorMeeting();
            }
        });

        layoutMiscellaneous.findViewById(R.id.viewColor3Meetings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedMeetingColor = "#ff4842";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(R.drawable.ic_done);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColorMeeting();
            }
        });

        layoutMiscellaneous.findViewById(R.id.viewColor4Meetings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedMeetingColor = "#3a52fc";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(R.drawable.ic_done);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColorMeeting();
            }
        });

        layoutMiscellaneous.findViewById(R.id.viewColor5Meetings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedMeetingColor = "#000000";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(R.drawable.ic_done);
                setSubtitleIndicatorColorMeeting();
            }
        });

        if (alreadyAvailableMeeting != null && alreadyAvailableMeeting.getColor() != null && !alreadyAvailableMeeting.getColor().trim().isEmpty()) {
            switch (alreadyAvailableMeeting.getColor()){
                case "#fdbe3b":
                    layoutMiscellaneous.findViewById(R.id.viewColor2Meetings).performClick();
                    break;
                case "#ff4842":
                    layoutMiscellaneous.findViewById(R.id.viewColor3Meetings).performClick();
                    break;
                case "##3a52fc":
                    layoutMiscellaneous.findViewById(R.id.viewColor4Meetings).performClick();
                    break;
                case "#000000":
                    layoutMiscellaneous.findViewById(R.id.viewColor5Meetings).performClick();
                    break;
            }
        }


        if(alreadyAvailableMeeting != null) {
            layoutMiscellaneous.findViewById(R.id.layoutDeleteMeetings).setVisibility(View.VISIBLE);
            layoutMiscellaneous.findViewById(R.id.layoutDeleteMeetings).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    showDeleteMeetingDialog();
                }
            });
        }

    }

    private void showDeleteMeetingDialog() {
        if(dialogDeleteMeeting == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(com.example.notessample.activities.CreateMeetingActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_delete_meetings,
                    (ViewGroup) findViewById((R.id.layoutDeleteMeetingsContainer))
            );
            builder.setView(view);
            dialogDeleteMeeting = builder.create();
            if(dialogDeleteMeeting.getWindow() != null) {
                dialogDeleteMeeting.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.textDeleteMeetings).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    @SuppressLint("StaticFieldLeak")
                    class DeleteMeetingTask extends AsyncTask<Void, Void, Void> {

                        @Override
                        protected Void doInBackground(Void... voids) {
                            MeetingsDatabase.getDatabase(getApplicationContext()).meetingDao()
                                    .deleteMeeting(alreadyAvailableMeeting);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            Intent intent = new Intent();
                            intent.putExtra("isMeetingDeleted", true);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }

                    new DeleteMeetingTask().execute();
                }

            });

            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogDeleteMeeting.dismiss();
                }
            });

        }

        dialogDeleteMeeting.show();
    }

    private void setSubtitleIndicatorColorMeeting() {
        GradientDrawable gradientDrawable = (GradientDrawable) viewSubtitleIndicatorMeeting.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedMeetingColor));
    }

}