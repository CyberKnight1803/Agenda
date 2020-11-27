package com.example.notessample.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.notessample.entities.Todo;

import java.util.List;

@Dao
public interface TodoDao {

    @Query("SELECT * FROM Todo ORDER BY id DESC")
    List<Todo> getAllTodos();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTodo(Todo todo);

    @Delete
    void deleteTodo(Todo todo);

}
