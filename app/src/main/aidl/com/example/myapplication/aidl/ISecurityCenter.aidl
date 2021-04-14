// ISecurityCenter.aidl
package com.example.myapplication.aidl;

// Declare any non-default types here with import statements

interface ISecurityCenter {
    String encrypt(String content);
    String decrypt(String password);
}