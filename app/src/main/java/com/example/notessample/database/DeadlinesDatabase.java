package com.example.notessample.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.notessample.entities.Deadline;
import com.example.notessample.dao.DeadlineDao;


@Database(entities = Deadline.class, version=1, exportSchema = false)
public abstract class DeadlinesDatabase extends RoomDatabase {

    private static DeadlinesDatabase deadlinesDatabase;

    public static synchronized DeadlinesDatabase getDatabase(Context context) {
        if (deadlinesDatabase == null)
        {
            deadlinesDatabase = Room.databaseBuilder(
                    context,
                    DeadlinesDatabase.class,
                    "deadlines_db"
            ).build();
        }
        return deadlinesDatabase;
    }

    public abstract DeadlineDao deadlineDao();

}
