package com.example.notessample.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.notessample.entities.Meeting;
import com.example.notessample.dao.MeetingDao;

@Database(entities = Meeting.class, version=1, exportSchema = false)
public abstract class MeetingsDatabase extends RoomDatabase {

    private static com.example.notessample.database.MeetingsDatabase meetingsDatabase;

    public static synchronized com.example.notessample.database.MeetingsDatabase getDatabase(Context context) {
        if (meetingsDatabase == null)
        {
            meetingsDatabase = Room.databaseBuilder(
                    context,
                    com.example.notessample.database.MeetingsDatabase.class,
                    "meetings_db"
            ).build();
        }
        return meetingsDatabase;
    }

    public abstract MeetingDao meetingDao();

}
