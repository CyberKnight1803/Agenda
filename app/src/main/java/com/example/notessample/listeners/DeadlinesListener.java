package com.example.notessample.listeners;

import com.example.notessample.entities.Deadline;

public interface DeadlinesListener {
    void onDeadlineClicked(Deadline deadline, int position);

}
