// IBookManager.aidl
package com.example.myapplication.aidl;

// Declare any non-default types here with import statements
import com.example.myapplication.aidl.Book;
import com.example.myapplication.aidl.IOnNewBookArrivedListener;

interface IBookManager {
    List<Book> getBookList();
    void addBook(in Book book);
    void registerListener(IOnNewBookArrivedListener listener);
    void unregisterListener(IOnNewBookArrivedListener listener);
}