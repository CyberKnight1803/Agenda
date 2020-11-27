package com.example.notessample.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.notessample.entities.Diary;

import java.util.List;

@Dao
public interface DiaryDao {

    @Query("SELECT * FROM diary ORDER BY id DESC")
    List<Diary> getAllDiaries();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDiary(Diary diary);

    @Delete
    void deleteDiary(Diary diary);



}
