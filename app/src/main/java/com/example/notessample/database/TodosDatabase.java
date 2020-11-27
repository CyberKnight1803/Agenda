package com.example.notessample.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.notessample.dao.TodoDao;
import com.example.notessample.entities.Todo;

@Database(entities = Todo.class, version=1, exportSchema = false)
public abstract class TodosDatabase extends RoomDatabase {

    private static com.example.notessample.database.TodosDatabase todosDatabase;

    public static synchronized com.example.notessample.database.TodosDatabase getDatabase(Context context) {
        if (todosDatabase == null)
        {
            todosDatabase = Room.databaseBuilder(
                    context,
                    com.example.notessample.database.TodosDatabase.class,
                    "todos_db"
            ).build();
        }
        return todosDatabase;
    }

    public abstract TodoDao todoDao();

}
