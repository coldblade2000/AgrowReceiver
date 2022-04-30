package com.pmc3.uniandes.agrowreceiver.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DataPacketDAO {
    @Query("SELECT * FROM DataPacket")
    List<DataPacket> getAll();

    @Query("SELECT * FROM datapacket WHERE deviceID = :deviceID")
    List<DataPacket> loadAllByDeviceID(String deviceID);

    @Query("SELECT * FROM datapacket WHERE dataHash LIKE :dataHash LIMIT 1")
    DataPacket findByDataHash(String dataHash);

    @Query("SELECT * FROM datapacket WHERE uploadedAlready = 0")
    List<DataPacket> findAllUnuploadedData();

    @Insert
    void insertAll(DataPacket... dataPackets);

    @Delete
    void delete(DataPacket dataPackets);

}
