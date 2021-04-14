// IBinderPool.aidl
package com.example.myapplication.aidl;

// Declare any non-default types here with import statements

interface IBinderPool {
    IBinder queryBinder(int binderCode);
}