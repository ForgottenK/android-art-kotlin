// IOnNewBookArrivedListener.aidl
package com.example.myapplication.aidl;

import com.example.myapplication.aidl.Book;

interface IOnNewBookArrivedListener {
    void onNewBookArrived(in Book newBook);
}