package com.example.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface UserDAO {
    @Query("SELECT * FROM model")
    Single<List<Model>> getAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Single<List<Long>> insert(List<Model> model);

    @Query("DELETE FROM model")
    Single<Integer> delete();

}
