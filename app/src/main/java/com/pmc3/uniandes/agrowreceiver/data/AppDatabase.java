package com.pmc3.uniandes.agrowreceiver.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {DataPacket.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public DataPacketDAO dataPacketDAO;

    private static AppDatabase instance;

    public static AppDatabase getDatabase(Context context){
        if (instance == null){
            synchronized (AppDatabase.class){
                if (instance == null){
                    Room.databaseBuilder(context, AppDatabase.class, "database").build();
                }
            }
        }
        return instance;
    }
}
