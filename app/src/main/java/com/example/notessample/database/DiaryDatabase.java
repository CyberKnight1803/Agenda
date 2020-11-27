package com.example.notessample.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.notessample.dao.DiaryDao;
import com.example.notessample.entities.Diary;

@Database(entities = Diary.class, version=1, exportSchema = false)
public abstract class DiaryDatabase extends RoomDatabase {

    private static DiaryDatabase diaryDatabase;

    public static synchronized DiaryDatabase getDatabase(Context context) {
        if (diaryDatabase == null)
        {
            diaryDatabase = Room.databaseBuilder(
                    context,
                    DiaryDatabase.class,
                    "diary_db"
            ).build();
        }
        return diaryDatabase;
    }

    public abstract DiaryDao diaryDao();

}
