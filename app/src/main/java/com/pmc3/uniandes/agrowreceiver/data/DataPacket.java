package com.pmc3.uniandes.agrowreceiver.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(primaryKeys = {"deviceID", "dataHash"},
        tableName = "datapacket",
        indices = {@Index(value = {"dataHash"}, unique = true)})
public class DataPacket {
    @NonNull
    public String deviceID;
    @NonNull
    public String dataHash;

    public String payloadJSON;


    @ColumnInfo(defaultValue = "0")
    public boolean uploadedAlready;

    @Override
    public String toString() {
        return "DataPacket{" +
                "deviceID='" + deviceID + '\'' +
                ", dataHash='" + dataHash + '\'' +
                ", payloadJSON='" + payloadJSON + '\'' +
                ", uploadedAlready=" + uploadedAlready +
                '}';
    }
}
