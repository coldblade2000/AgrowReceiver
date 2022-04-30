package com.pmc3.uniandes.agrowreceiver.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {DataPacket.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public DataPacketDAO dataPacketDAO;
}
