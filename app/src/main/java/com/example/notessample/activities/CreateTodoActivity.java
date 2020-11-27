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
import com.example.notessample.database.TodosDatabase;
import com.example.notessample.entities.Todo;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateTodoActivity extends AppCompatActivity {

    private EditText inputTodoTitle, inputTodoSubtitle, inputTodoText;
    private TextView textDateTimeTodo;
    private View viewSubtitleIndicatorTodo;

    private String selectedTodoColor;

    private AlertDialog dialogDeleteTodo;

    private Todo alreadyAvailableTodo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_todo);

        ImageView imageBack = findViewById(R.id.imageBackTodo);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        inputTodoTitle = findViewById(R.id.inputTodoTitle);
        inputTodoSubtitle = findViewById(R.id.inputTodoSubtitle);
        inputTodoText = findViewById(R.id.inputTodo);
        textDateTimeTodo = findViewById(R.id.textDateTimeTodo);
        viewSubtitleIndicatorTodo = findViewById(R.id.viewSubtitleIndicatorTodo);
        textDateTimeTodo.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                        .format(new Date())
        );

        ImageView imageSave = findViewById(R.id.imageSaveTodo);
        imageSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void  onClick(View v) {
                saveTodo();
            }
        });

        selectedTodoColor = "#333333";

        if(getIntent().getBooleanExtra("isViewOrUpdate", false)) {
            alreadyAvailableTodo = (Todo) getIntent().getSerializableExtra("todo");
            setViewOrUpdateTodo();
        }

        initMiscellaneousTodo();
        setSubtitleIndicatorColorTodo();

    }

    private void setViewOrUpdateTodo() {
        inputTodoTitle.setText(alreadyAvailableTodo.getTitle());
        inputTodoSubtitle.setText(alreadyAvailableTodo.getSubtitle());
        inputTodoText.setText(alreadyAvailableTodo.getTodoText());
        textDateTimeTodo.setText(alreadyAvailableTodo.getDateTime());
        /*
            if(alreadyAvailableNote.getWebLink() != null && !alreadyAvailableNote.getWebLink().trim().isEmpty()) {
            textWebURL.setText(alreadyAvailableNote.getWebLink());
            layoutWebURL.setVisibility(View.VISIBLE);
        }
         */
    }

    private void saveTodo() {
        if (inputTodoTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Title can not be empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (inputTodoSubtitle.getText().toString().trim().isEmpty() &&
                inputTodoText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "To-Do can not be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        final Todo todo = new Todo();
        todo.setTitle(inputTodoTitle.getText().toString());
        todo.setSubtitle(inputTodoSubtitle.getText().toString());
        todo.setTodoText(inputTodoText.getText().toString());
        todo.setDateTime(textDateTimeTodo.getText().toString());
        todo.setColor(selectedTodoColor);

        if(alreadyAvailableTodo != null) {
            todo.setId(alreadyAvailableTodo.getId()); //Making the ID same would call in replace from dao
        }

        @SuppressLint("StaticFieldLeak")
        class SaveTodoTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected  Void doInBackground(Void... voids) {
                TodosDatabase.getDatabase(getApplicationContext()).todoDao().insertTodo(todo);
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

        new SaveTodoTask().execute();
    }

    private void initMiscellaneousTodo() {
        final LinearLayout layoutMiscellaneous = findViewById(R.id.miscTodos);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous);
        layoutMiscellaneous.findViewById(R.id.textMiscTodos).setOnClickListener(new View.OnClickListener() {
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

        final ImageView imageColor1 = layoutMiscellaneous.findViewById(R.id.imageColor1Todos);
        final ImageView imageColor2 = layoutMiscellaneous.findViewById(R.id.imageColor2Todos);
        final ImageView imageColor3 = layoutMiscellaneous.findViewById(R.id.imageColor3Todos);
        final ImageView imageColor4 = layoutMiscellaneous.findViewById(R.id.imageColor4Todos);
        final ImageView imageColor5 = layoutMiscellaneous.findViewById(R.id.imageColor5Todos);

        layoutMiscellaneous.findViewById(R.id.viewColor1Todos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTodoColor = "#333333";
                imageColor1.setImageResource(R.drawable.ic_done);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColorTodo();
            }
        });

        layoutMiscellaneous.findViewById(R.id.viewColor2Todos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTodoColor = "#fdbe3b";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(R.drawable.ic_done);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColorTodo();
            }
        });

        layoutMiscellaneous.findViewById(R.id.viewColor3Todos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTodoColor = "#ff4842";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(R.drawable.ic_done);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColorTodo();
            }
        });

        layoutMiscellaneous.findViewById(R.id.viewColor4Todos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTodoColor = "#3a52fc";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(R.drawable.ic_done);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColorTodo();
            }
        });

        layoutMiscellaneous.findViewById(R.id.viewColor5Todos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTodoColor = "#000000";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(R.drawable.ic_done);
                setSubtitleIndicatorColorTodo();
            }
        });

        if (alreadyAvailableTodo != null && alreadyAvailableTodo.getColor() != null && !alreadyAvailableTodo.getColor().trim().isEmpty()) {
            switch (alreadyAvailableTodo.getColor()){
                case "#fdbe3b":
                    layoutMiscellaneous.findViewById(R.id.viewColor2Todos).performClick();
                    break;
                case "#ff4842":
                    layoutMiscellaneous.findViewById(R.id.viewColor3Todos).performClick();
                    break;
                case "##3a52fc":
                    layoutMiscellaneous.findViewById(R.id.viewColor4Todos).performClick();
                    break;
                case "#000000":
                    layoutMiscellaneous.findViewById(R.id.viewColor5Todos).performClick();
                    break;
            }
        }


        if(alreadyAvailableTodo != null) {
            layoutMiscellaneous.findViewById(R.id.layoutDeleteTodos).setVisibility(View.VISIBLE);
            layoutMiscellaneous.findViewById(R.id.layoutDeleteTodos).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    showDeleteTodoDialog();
                }
            });
        }

    }

    private void showDeleteTodoDialog() {
        if(dialogDeleteTodo == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(com.example.notessample.activities.CreateTodoActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_delete_todo,
                    (ViewGroup) findViewById((R.id.layoutDeleteTodosContainer))
            );
            builder.setView(view);
            dialogDeleteTodo = builder.create();
            if(dialogDeleteTodo.getWindow() != null) {
                dialogDeleteTodo.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.textDeleteTodos).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    @SuppressLint("StaticFieldLeak")
                    class DeleteTodoTask extends AsyncTask<Void, Void, Void> {

                        @Override
                        protected Void doInBackground(Void... voids) {
                            com.example.notessample.database.TodosDatabase.getDatabase(getApplicationContext()).todoDao()
                                    .deleteTodo(alreadyAvailableTodo);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            Intent intent = new Intent();
                            intent.putExtra("isTodoDeleted", true);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }

                    new DeleteTodoTask().execute();
                }

            });

            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogDeleteTodo.dismiss();
                }
            });

        }

        dialogDeleteTodo.show();
    }

    private void setSubtitleIndicatorColorTodo() {
        GradientDrawable gradientDrawable = (GradientDrawable) viewSubtitleIndicatorTodo.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedTodoColor));
    }

}