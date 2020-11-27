package com.example.notessample.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.notessample.entities.Deadline;

import java.util.List;


@Dao
public interface DeadlineDao {

    @Query("SELECT * FROM Deadline ORDER BY id DESC")
    List<Deadline> getAllDeadlines();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDeadline(Deadline deadline);

    @Delete
    void deleteDeadline(Deadline deadline);

}
