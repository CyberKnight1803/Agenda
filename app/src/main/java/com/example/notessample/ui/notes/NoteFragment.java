package com.example.notessample.ui.notes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.notessample.R;
import com.example.notessample.activities.CreateDiaryActivity;
import com.example.notessample.database.DiaryDatabase;
import com.example.notessample.entities.Diary;
import com.example.notessample.listeners.DiaryListener;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class NoteFragment extends Fragment implements DiaryListener {

    //used for diaryMainActivity
    public static final int REQUEST_CODE_ADD_DIARY = 1;
    public static final int REQUEST_CODE_UPDATE_DIARY = 2;
    public static final int REQUEST_CODE_SHOW_DIARY = 3;

    private RecyclerView diarysRecyclerView;
    private List<Diary> diaryList;
    private com.example.notessample.adapters.DiaryAdapter diaryAdapter;

    private int diaryClickedPosition = -1;

    private com.example.notessample.ui.notes.NoteViewModel noteViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        noteViewModel =
                new ViewModelProvider(this).get(com.example.notessample.ui.notes.NoteViewModel.class);
        View root = inflater.inflate(R.layout.fragment_note, container, false);

        //usedForDiaryMainActivity
        ImageView imageAddDiaryMain = root.findViewById(R.id.imageAddDiaryMain);
        imageAddDiaryMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        new Intent(getContext(), CreateDiaryActivity.class),
                        REQUEST_CODE_ADD_DIARY
                );
            }
        });

        diarysRecyclerView = root.findViewById(R.id.diaryRecyclerView);
        diarysRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        );

        diaryList = new ArrayList<>();
        diaryAdapter = new com.example.notessample.adapters.DiaryAdapter(diaryList, this);
        diarysRecyclerView.setAdapter(diaryAdapter);

        getDiarys(REQUEST_CODE_SHOW_DIARY, false); //Part of onCreate, need to show all diarys

        EditText inputSearch = root.findViewById(R.id.inputSearchDiary);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                diaryAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(diaryList.size() != 0) {
                    diaryAdapter.searchDiary(editable.toString());
                }
            }
        });


        return root;
    }

    @Override
    public void onDiaryClicked(Diary diary, int position) {
        diaryClickedPosition = position;
        Intent intent = new Intent(getContext(), CreateDiaryActivity.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("diary", diary);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_DIARY);
    }

    private void getDiarys(final int requestCode, final boolean isDiaryDeleted) {

        @SuppressLint("StaticFieldLink")
        class GetDiarysTask extends AsyncTask<Void, Void, List<Diary>> {

            @Override
            protected List<Diary> doInBackground(Void... voids) {
                return DiaryDatabase
                        .getDatabase(getContext())
                        .diaryDao().getAllDiaries();
            }

            @Override
            protected void  onPostExecute(List<Diary> diaries) {
                super.onPostExecute(diaries);
                if(requestCode == REQUEST_CODE_SHOW_DIARY) { //add all diaries from DB to noteList and notify Adapter of change
                    diaryList.addAll(diaries);
                    diaryAdapter.notifyDataSetChanged();
                }
                else if (requestCode == REQUEST_CODE_ADD_DIARY) {//add first (newest added) note from DB to noteList, notify Adapter, scroll to the top
                    diaryList.add(0, diaries.get(0));
                    diaryAdapter.notifyItemInserted(0);
                    diarysRecyclerView.smoothScrollToPosition(0);
                }
                else if(requestCode == REQUEST_CODE_UPDATE_DIARY) {//remove from clicked position from noteList, add to clicked position from DB, notify adapter of change at clicked position
                    diaryList.remove(diaryClickedPosition);
                    if(isDiaryDeleted){
                        diaryAdapter.notifyItemRemoved(diaryClickedPosition);
                    }
                    else
                    {
                        diaryList.add(diaryClickedPosition, diaries.get(diaryClickedPosition));
                        diaryAdapter.notifyItemChanged(diaryClickedPosition);
                    }
                }
            }
        }

        new GetDiarysTask().execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_ADD_DIARY && resultCode == RESULT_OK) { //RESULT_OK IN CreateNoteActivity and REQUEST_CODE_ADD_NOTE in DashboardFragment
            getDiarys(REQUEST_CODE_ADD_DIARY, false); //from onActivity, request is for adding note and it is okayed
        }
        else if (requestCode == REQUEST_CODE_UPDATE_DIARY && resultCode == RESULT_OK) {
            if(data != null){
                getDiarys(REQUEST_CODE_UPDATE_DIARY, data.getBooleanExtra("isDiaryDeleted", false)); //from onActivity, request is for updating note and it is okayed
            }
        }
    }
}