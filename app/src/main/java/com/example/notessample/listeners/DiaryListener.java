package com.example.notessample.listeners;

import com.example.notessample.entities.Diary;

public interface DiaryListener {
    void onDiaryClicked(Diary diary, int position);

}
