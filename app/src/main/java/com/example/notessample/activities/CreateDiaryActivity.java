package com.example.notessample.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.notessample.R;
import com.example.notessample.database.DiaryDatabase;
import com.example.notessample.entities.Diary;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateDiaryActivity extends AppCompatActivity {

    private EditText inputDiaryTitle, inputDiarySubtitle, inputDiaryText;
    private TextView textDateTimeDiary;
    private View viewSubtitleIndicatorDiary;
    private ImageView imageDiary;

    private String selectedDiaryColor;
    private String selectedImagePathDiary;

    private static final int REQUEST_CODE_STORAGE_PERMISSION_DIARY = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE_DIARY = 2;


    private AlertDialog dialogDeleteDiary;

    private Diary alreadyAvailableDiary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        ImageView imageBack = findViewById(R.id.imageBackDiary);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        inputDiaryTitle = findViewById(R.id.inputDiaryTitle);
        inputDiarySubtitle = findViewById(R.id.inputDiarySubtitle);
        inputDiaryText = findViewById(R.id.inputDiary);
        textDateTimeDiary = findViewById(R.id.textDateTimeDiary);
        viewSubtitleIndicatorDiary = findViewById(R.id.viewSubtitleIndicatorDiary);
        imageDiary = findViewById(R.id.imageDiary);
        textDateTimeDiary.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                        .format(new Date())
        );

        ImageView imageSave = findViewById(R.id.imageSaveDiary);
        imageSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void  onClick(View v) {
                saveDiary();
            }
        });

        selectedDiaryColor = "#333333";
        selectedImagePathDiary = "";

        if(getIntent().getBooleanExtra("isViewOrUpdate", false)) {
            alreadyAvailableDiary = (Diary) getIntent().getSerializableExtra("diary");
            setViewOrUpdateDiary();
        }

        findViewById(R.id.imageRemoveImageDiary).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageDiary.setImageBitmap(null);
                imageDiary.setVisibility(View.GONE);
                findViewById(R.id.imageRemoveImageDiary).setVisibility(View.GONE);
                selectedImagePathDiary = "";
            }
        });

        initMiscellaneousDiary();
        setSubtitleIndicatorColorDiary();

    }

    private void setViewOrUpdateDiary() {
        inputDiaryTitle.setText(alreadyAvailableDiary.getTitle());
        inputDiarySubtitle.setText(alreadyAvailableDiary.getSubtitle());
        inputDiaryText.setText(alreadyAvailableDiary.getDiaryText());
        textDateTimeDiary.setText(alreadyAvailableDiary.getDateTime());
        if(alreadyAvailableDiary.getImagePath() != null && !alreadyAvailableDiary.getImagePath().trim().isEmpty()) {
            imageDiary.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableDiary.getImagePath()));
            imageDiary.setVisibility(View.VISIBLE);
            findViewById(R.id.imageRemoveImageDiary).setVisibility(View.VISIBLE);
            selectedImagePathDiary = alreadyAvailableDiary.getImagePath();
        }
        /*
            if(alreadyAvailableNote.getWebLink() != null && !alreadyAvailableNote.getWebLink().trim().isEmpty()) {
            textWebURL.setText(alreadyAvailableNote.getWebLink());
            layoutWebURL.setVisibility(View.VISIBLE);
        }
         */
    }

    private void saveDiary() {
        if (inputDiaryTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Title can not be empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (inputDiarySubtitle.getText().toString().trim().isEmpty() &&
                inputDiaryText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Diary can not be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        final Diary diary = new Diary();
        diary.setTitle(inputDiaryTitle.getText().toString());
        diary.setSubtitle(inputDiarySubtitle.getText().toString());
        diary.setDiaryText(inputDiaryText.getText().toString());
        diary.setDateTime(textDateTimeDiary.getText().toString());
        diary.setColor(selectedDiaryColor);
        diary.setImagePath(selectedImagePathDiary);

        if(alreadyAvailableDiary != null) {
            diary.setId(alreadyAvailableDiary.getId()); //Making the ID same would call in replace from dao
        }

        @SuppressLint("StaticFieldLeak")
        class SaveDiaryTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected  Void doInBackground(Void... voids) {
                DiaryDatabase.getDatabase(getApplicationContext()).diaryDao().insertDiary(diary);
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

        new SaveDiaryTask().execute();
    }

    private void initMiscellaneousDiary() {
        final LinearLayout layoutMiscellaneous = findViewById(R.id.miscDiary);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous);
        layoutMiscellaneous.findViewById(R.id.textMiscDiary).setOnClickListener(new View.OnClickListener() {
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

        final ImageView imageColor1 = layoutMiscellaneous.findViewById(R.id.imageColor1Diary);
        final ImageView imageColor2 = layoutMiscellaneous.findViewById(R.id.imageColor2Diary);
        final ImageView imageColor3 = layoutMiscellaneous.findViewById(R.id.imageColor3Diary);
        final ImageView imageColor4 = layoutMiscellaneous.findViewById(R.id.imageColor4Diary);
        final ImageView imageColor5 = layoutMiscellaneous.findViewById(R.id.imageColor5Diary);

        layoutMiscellaneous.findViewById(R.id.viewColor1Diary).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDiaryColor = "#333333";
                imageColor1.setImageResource(R.drawable.ic_done);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColorDiary();
            }
        });

        layoutMiscellaneous.findViewById(R.id.viewColor2Diary).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDiaryColor = "#fdbe3b";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(R.drawable.ic_done);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColorDiary();
            }
        });

        layoutMiscellaneous.findViewById(R.id.viewColor3Diary).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDiaryColor = "#ff4842";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(R.drawable.ic_done);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColorDiary();
            }
        });

        layoutMiscellaneous.findViewById(R.id.viewColor4Diary).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDiaryColor = "#3a52fc";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(R.drawable.ic_done);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColorDiary();
            }
        });

        layoutMiscellaneous.findViewById(R.id.viewColor5Diary).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDiaryColor = "#000000";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(R.drawable.ic_done);
                setSubtitleIndicatorColorDiary();
            }
        });

        if (alreadyAvailableDiary != null && alreadyAvailableDiary.getColor() != null && !alreadyAvailableDiary.getColor().trim().isEmpty()) {
            switch (alreadyAvailableDiary.getColor()){
                case "#fdbe3b":
                    layoutMiscellaneous.findViewById(R.id.viewColor2Diary).performClick();
                    break;
                case "#ff4842":
                    layoutMiscellaneous.findViewById(R.id.viewColor3Diary).performClick();
                    break;
                case "##3a52fc":
                    layoutMiscellaneous.findViewById(R.id.viewColor4Diary).performClick();
                    break;
                case "#000000":
                    layoutMiscellaneous.findViewById(R.id.viewColor5Diary).performClick();
                    break;
            }
        }

        layoutMiscellaneous.findViewById(R.id.layoutAddImageDiary).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if(ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            CreateDiaryActivity.this,
                            new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION_DIARY
                    );
                }
                else
                {
                    selectImage();
                }

            }
        });

        if(alreadyAvailableDiary != null) {
            layoutMiscellaneous.findViewById(R.id.layoutDeleteDiary).setVisibility(View.VISIBLE);
            layoutMiscellaneous.findViewById(R.id.layoutDeleteDiary).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    showDeleteDiaryDialog();
                }
            });
        }

    }

    private void showDeleteDiaryDialog() {
        if(dialogDeleteDiary == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateDiaryActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_delete_note,
                    (ViewGroup) findViewById((R.id.layoutDeleteDiaryContainer))
            );
            builder.setView(view);
            dialogDeleteDiary = builder.create();
            if(dialogDeleteDiary.getWindow() != null) {
                dialogDeleteDiary.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.textDeleteDiary).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    @SuppressLint("StaticFieldLeak")
                    class DeleteDiaryTask extends AsyncTask<Void, Void, Void> {

                        @Override
                        protected Void doInBackground(Void... voids) {
                            DiaryDatabase.getDatabase(getApplicationContext()).diaryDao()
                                    .deleteDiary(alreadyAvailableDiary);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            Intent intent = new Intent();
                            intent.putExtra("isDiaryDeleted", true);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }

                    new DeleteDiaryTask().execute();
                }

            });

            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogDeleteDiary.dismiss();
                }
            });

        }

        dialogDeleteDiary.show();
    }

    private void setSubtitleIndicatorColorDiary() {
        GradientDrawable gradientDrawable = (GradientDrawable) viewSubtitleIndicatorDiary.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedDiaryColor));
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE_DIARY);
        }
    }

    @Override
    public void  onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_STORAGE_PERMISSION_DIARY && grantResults.length > 0) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            }
            else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_SELECT_IMAGE_DIARY && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                if(selectedImageUri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageDiary.setImageBitmap(bitmap);
                        imageDiary.setVisibility(View.VISIBLE);
                        findViewById(R.id.imageRemoveImageDiary).setVisibility(View.VISIBLE);

                        selectedImagePathDiary = getPathfromUri(selectedImageUri);

                    } catch (Exception exception) {
                        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private String getPathfromUri(Uri contentUri) {
        String filePath;
        Cursor cursor = getContentResolver()
                .query(contentUri, null, null, null, null);
        if (cursor == null) {
            filePath = contentUri.getPath();
        }
        else
        {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }
}