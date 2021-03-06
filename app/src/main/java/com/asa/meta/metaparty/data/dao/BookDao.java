package com.asa.meta.metaparty.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.asa.meta.metaparty.data.bean.Book;

import java.util.List;

@Dao
public interface BookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertBook(Book... books);

    @Update
    public void updateBook(Book... books);

    @Delete
    public void deleteBook(Book... books);

    @Query("SELECT * FROM book")
    public LiveData<List<Book>> queryAllBook();
}
