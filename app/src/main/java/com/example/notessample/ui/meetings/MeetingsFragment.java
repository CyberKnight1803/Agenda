package com.example.notessample.ui.meetings;

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
import com.example.notessample.activities.CreateMeetingActivity;
import com.example.notessample.adapters.MeetingsAdapter;
import com.example.notessample.database.MeetingsDatabase;
import com.example.notessample.entities.Meeting;
import com.example.notessample.listeners.MeetingsListener;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class MeetingsFragment extends Fragment implements MeetingsListener {

    //used for diaryMainActivity
    public static final int REQUEST_CODE_ADD_MEETING = 1;
    public static final int REQUEST_CODE_UPDATE_MEETING = 2;
    public static final int REQUEST_CODE_SHOW_MEETING = 3;

    private RecyclerView meetingsRecyclerView;
    private List<Meeting> meetingList;
    private MeetingsAdapter meetingsAdapter;

    private int meetingClickedPosition = -1;

    private com.example.notessample.ui.meetings.MeetingsViewModel meetingsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        meetingsViewModel =
                new ViewModelProvider(this).get(com.example.notessample.ui.meetings.MeetingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_meetings, container, false);

        //usedForDiaryMainActivity
        ImageView imageAddMeetingMain = root.findViewById(R.id.imageAddMeetingMain);
        imageAddMeetingMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        new Intent(getContext(), CreateMeetingActivity.class),
                        REQUEST_CODE_ADD_MEETING
                );
            }
        });

        meetingsRecyclerView = root.findViewById(R.id.meetingRecyclerView);
        meetingsRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        );

        meetingList = new ArrayList<>();
        meetingsAdapter = new MeetingsAdapter(meetingList, this);
        meetingsRecyclerView.setAdapter(meetingsAdapter);

        getMeetings(REQUEST_CODE_SHOW_MEETING, false); //Part of onCreate, need to show all diarys

        EditText inputSearch = root.findViewById(R.id.inputSearchMeeting);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                meetingsAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(meetingList.size() != 0) {
                    meetingsAdapter.searchMeeting(editable.toString());
                }
            }
        });


        return root;
    }

    @Override
    public void onMeetingClicked(Meeting meeting, int position) {
        meetingClickedPosition = position;
        Intent intent = new Intent(getContext(), CreateMeetingActivity.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("meeting", meeting);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_MEETING);
    }

    private void getMeetings(final int requestCode, final boolean isMeetingDeleted) {

        @SuppressLint("StaticFieldLink")
        class GetMeetingsTask extends AsyncTask<Void, Void, List<Meeting>> {

            @Override
            protected List<Meeting> doInBackground(Void... voids) {
                return MeetingsDatabase
                        .getDatabase(getContext())
                        .meetingDao().getAllMeetings();
            }

            @Override
            protected void  onPostExecute(List<Meeting> meetings) {
                super.onPostExecute(meetings);
                if(requestCode == REQUEST_CODE_SHOW_MEETING) { //add all meetings from DB to noteList and notify Adapter of change
                    meetingList.addAll(meetings);
                    meetingsAdapter.notifyDataSetChanged();
                }
                else if (requestCode == REQUEST_CODE_ADD_MEETING) {//add first (newest added) note from DB to noteList, notify Adapter, scroll to the top
                    meetingList.add(0, meetings.get(0));
                    meetingsAdapter.notifyItemInserted(0);
                    meetingsRecyclerView.smoothScrollToPosition(0);
                }
                else if(requestCode == REQUEST_CODE_UPDATE_MEETING) {//remove from clicked position from noteList, add to clicked position from DB, notify adapter of change at clicked position
                    meetingList.remove(meetingClickedPosition);
                    if(isMeetingDeleted){
                        meetingsAdapter.notifyItemRemoved(meetingClickedPosition);
                    }
                    else
                    {
                        meetingList.add(meetingClickedPosition, meetings.get(meetingClickedPosition));
                        meetingsAdapter.notifyItemChanged(meetingClickedPosition);
                    }
                }
            }
        }

        new GetMeetingsTask().execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_ADD_MEETING && resultCode == RESULT_OK) { //RESULT_OK IN CreateNoteActivity and REQUEST_CODE_ADD_NOTE in DashboardFragment
            getMeetings(REQUEST_CODE_ADD_MEETING, false); //from onActivity, request is for adding note and it is okayed
        }
        else if (requestCode == REQUEST_CODE_UPDATE_MEETING && resultCode == RESULT_OK) {
            if(data != null){
                getMeetings(REQUEST_CODE_UPDATE_MEETING, data.getBooleanExtra("isMeetingDeleted", false)); //from onActivity, request is for updating note and it is okayed
            }
        }
    }
}