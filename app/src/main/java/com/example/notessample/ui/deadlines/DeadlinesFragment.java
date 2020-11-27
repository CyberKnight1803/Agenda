package com.example.notessample.ui.deadlines;

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
import com.example.notessample.activities.CreateDeadlineActivity;
import com.example.notessample.adapters.DeadlinesAdapter;
import com.example.notessample.database.DeadlinesDatabase;
import com.example.notessample.entities.Deadline;
import com.example.notessample.listeners.DeadlinesListener;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class DeadlinesFragment extends Fragment implements DeadlinesListener {

    //used for diaryMainActivity
    public static final int REQUEST_CODE_ADD_DEADLINE = 1;
    public static final int REQUEST_CODE_UPDATE_DEADLINE = 2;
    public static final int REQUEST_CODE_SHOW_DEADLINE = 3;

    private RecyclerView deadlinesRecyclerView;
    private List<Deadline> deadlineList;
    private DeadlinesAdapter deadlinesAdapter;

    private int deadlineClickedPosition = -1;

    private com.example.notessample.ui.deadlines.DeadlinesViewModel deadlinesViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        deadlinesViewModel =
                new ViewModelProvider(this).get(com.example.notessample.ui.deadlines.DeadlinesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_deadlines, container, false);

        //usedForDiaryMainActivity
        ImageView imageAddDeadlineMain = root.findViewById(R.id.imageAddDeadlineMain);
        imageAddDeadlineMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        new Intent(getContext(), CreateDeadlineActivity.class),
                        REQUEST_CODE_ADD_DEADLINE
                );
            }
        });

        deadlinesRecyclerView = root.findViewById(R.id.deadlineRecyclerView);
        deadlinesRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        );

        deadlineList = new ArrayList<>();
        deadlinesAdapter = new DeadlinesAdapter(deadlineList, this);
        deadlinesRecyclerView.setAdapter(deadlinesAdapter);

        getDeadlines(REQUEST_CODE_SHOW_DEADLINE, false); //Part of onCreate, need to show all diarys

        EditText inputSearch = root.findViewById(R.id.inputSearchDeadline);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                deadlinesAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(deadlineList.size() != 0) {
                    deadlinesAdapter.searchDeadline(editable.toString());
                }
            }
        });


        return root;
    }

    @Override
    public void onDeadlineClicked(Deadline deadline, int position) {
        deadlineClickedPosition = position;
        Intent intent = new Intent(getContext(), CreateDeadlineActivity.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("deadline", deadline);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_DEADLINE);
    }

    private void getDeadlines(final int requestCode, final boolean isDeadlineDeleted) {

        @SuppressLint("StaticFieldLink")
        class GetDeadlinesTask extends AsyncTask<Void, Void, List<Deadline>> {

            @Override
            protected List<Deadline> doInBackground(Void... voids) {
                return DeadlinesDatabase
                        .getDatabase(getContext())
                        .deadlineDao().getAllDeadlines();
            }

            @Override
            protected void  onPostExecute(List<Deadline> deadlines) {
                super.onPostExecute(deadlines);
                if(requestCode == REQUEST_CODE_SHOW_DEADLINE) { //add all deadlines from DB to noteList and notify Adapter of change
                    deadlineList.addAll(deadlines);
                    deadlinesAdapter.notifyDataSetChanged();
                }
                else if (requestCode == REQUEST_CODE_ADD_DEADLINE) {//add first (newest added) note from DB to noteList, notify Adapter, scroll to the top
                    deadlineList.add(0, deadlines.get(0));
                    deadlinesAdapter.notifyItemInserted(0);
                    deadlinesRecyclerView.smoothScrollToPosition(0);
                }
                else if(requestCode == REQUEST_CODE_UPDATE_DEADLINE) {//remove from clicked position from noteList, add to clicked position from DB, notify adapter of change at clicked position
                    deadlineList.remove(deadlineClickedPosition);
                    if(isDeadlineDeleted){
                        deadlinesAdapter.notifyItemRemoved(deadlineClickedPosition);
                    }
                    else
                    {
                        deadlineList.add(deadlineClickedPosition, deadlines.get(deadlineClickedPosition));
                        deadlinesAdapter.notifyItemChanged(deadlineClickedPosition);
                    }
                }
            }
        }

        new GetDeadlinesTask().execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_ADD_DEADLINE && resultCode == RESULT_OK) { //RESULT_OK IN CreateNoteActivity and REQUEST_CODE_ADD_NOTE in DashboardFragment
            getDeadlines(REQUEST_CODE_ADD_DEADLINE, false); //from onActivity, request is for adding note and it is okayed
        }
        else if (requestCode == REQUEST_CODE_UPDATE_DEADLINE && resultCode == RESULT_OK) {
            if(data != null){
                getDeadlines(REQUEST_CODE_UPDATE_DEADLINE, data.getBooleanExtra("isDeadlineDeleted", false)); //from onActivity, request is for updating note and it is okayed
            }
        }
    }
}