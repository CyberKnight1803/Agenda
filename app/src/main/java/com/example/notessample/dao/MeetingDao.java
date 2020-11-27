package com.example.notessample.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.notessample.entities.Meeting;

import java.util.List;

@Dao
public interface MeetingDao {

    @Query("SELECT * FROM Meeting ORDER BY id DESC")
    List<Meeting> getAllMeetings();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMeeting(Meeting meeting);

    @Delete
    void deleteMeeting(Meeting meeting);

}
