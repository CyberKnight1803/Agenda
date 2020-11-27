package com.example.notessample.adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notessample.R;
import com.example.notessample.entities.Meeting;
import com.example.notessample.listeners.MeetingsListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MeetingsAdapter extends RecyclerView.Adapter<MeetingsAdapter.MeetingsViewHolder> {

    private List<Meeting> meeting;
    private MeetingsListener meetingsListener;
    private Timer timerMeeting;
    private List<Meeting> meetingSource;


    public MeetingsAdapter(List<Meeting> meeting, MeetingsListener meetingsListener) {
        this.meeting = meeting;
        this.meetingsListener = meetingsListener;
        meetingSource = meeting;

    }

    @NonNull
    @Override
    public MeetingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MeetingsViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_meetings,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull MeetingsViewHolder holder, int position) {
        holder.setMeeting(meeting.get(position));
        holder.layoutMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meetingsListener.onMeetingClicked(meeting.get(position), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return meeting.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class MeetingsViewHolder extends RecyclerView.ViewHolder {

        TextView textTitle, textSubtitle, textDateTime;
        LinearLayout layoutMeeting;
        RoundedImageView imageMeeting;

        MeetingsViewHolder(@NonNull View itemview) {
            super(itemview);
            textTitle = itemview.findViewById(R.id.textTitleMeetings);
            textSubtitle = itemview.findViewById(R.id.textSubtitleMeetings);
            textDateTime = itemview.findViewById(R.id.textDateTimeMeeting);
            layoutMeeting = itemview.findViewById(R.id.layoutMeetings);
            imageMeeting = itemview.findViewById(R.id.imageMeeting);
        }

        void setMeeting(Meeting meeting) {
            textTitle.setText(meeting.getTitle());
            if (meeting.getSubtitle().trim().isEmpty()) {
                textSubtitle.setVisibility(View.GONE);
            }
            else {
                textSubtitle.setText(meeting.getSubtitle());
            }
            textDateTime.setText(meeting.getDateTime());

            GradientDrawable gradientDrawable = (GradientDrawable) layoutMeeting.getBackground();
            if (meeting.getColor() != null) {
                gradientDrawable.setColor(Color.parseColor(meeting.getColor()));
            }
            else
            {
                gradientDrawable.setColor(Color.parseColor("#333333"));
            }

        }
    }

    public void searchMeeting(final String searchKeyword) {
        timerMeeting = new Timer();
        timerMeeting.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchKeyword.trim().isEmpty()) {
                    meeting = meetingSource;
                } else {
                    ArrayList<Meeting> temp = new ArrayList<>();
                    for (Meeting meeting : meetingSource) {
                        if (meeting.getTitle().toLowerCase().contains(searchKeyword.toLowerCase())
                                || meeting.getSubtitle().toLowerCase().contains(searchKeyword.toLowerCase())
                                || meeting.getMeetingText().toLowerCase().contains(searchKeyword.toLowerCase())) {
                            temp.add(meeting);
                        }
                    }
                    meeting = temp;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }, 500);
    }

    public void cancelTimer() {
        if(timerMeeting !=null) {
            timerMeeting.cancel();
        }
    }

}
