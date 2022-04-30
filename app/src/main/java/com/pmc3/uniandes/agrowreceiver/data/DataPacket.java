package com.pmc3.uniandes.agrowreceiver.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(primaryKeys = {"deviceID", "dataHash"},
        tableName = "datapacket",
        indices = {@Index(value = {"dataHash"}, unique = true)})
public class DataPacket {
    public String deviceID;
    public String dataHash;

    public String payloadJSON;


    @ColumnInfo(defaultValue = "0")
    public boolean uploadedAlready;
}