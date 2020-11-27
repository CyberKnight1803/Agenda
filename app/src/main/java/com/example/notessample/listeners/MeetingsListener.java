package com.example.notessample.listeners;

import com.example.notessample.entities.Meeting;

public interface MeetingsListener {
    void onMeetingClicked(Meeting meeting, int position);

}
