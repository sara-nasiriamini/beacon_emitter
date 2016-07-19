package com.medtronic.neuro.RechargeBeacon;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.nio.ByteBuffer;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    BluetoothManager mBluetoothManager;
    BluetoothAdapter mBluetoothAdapter;
    AdvertiseData mAdvertiseData;
    BluetoothLeAdvertiser mBluetoothLeAdvertiser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        Button button = (Button)findViewById(R.id.beacon_start);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAdvertiseData();
                Boolean isSupported = mBluetoothAdapter.isMultipleAdvertisementSupported();
                AdvertiseSettings advertiseSettings = new AdvertiseSettings.Builder().
                        setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER).
                        setTimeout(0).
                        setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM).
                        setConnectable(false).build();
                if (mBluetoothLeAdvertiser != null) {
                    mBluetoothLeAdvertiser.startAdvertising(advertiseSettings, mAdvertiseData, new MyAdvertiseCallback());
                }
            }
        });
    }

    protected void setAdvertiseData() {
        AdvertiseData.Builder mBuilder = new AdvertiseData.Builder();
        ByteBuffer mManufacturerData = ByteBuffer.allocate(24);
        byte[] uuid = getIdAsByte(UUID.fromString("0CF052C2-97CA-407C-84F8-B62AAC4E9020"));
        mManufacturerData.put(0, (byte)0xBE); // Beacon Identifier
        mManufacturerData.put(1, (byte)0xAC); // Beacon Identifier
        for (int i = 2; i <= 17; i++) {
            mManufacturerData.put(i, uuid[i-2]); // adding the UUID
        }
        mManufacturerData.put(18, (byte)0x00); // first byte of Major
        mManufacturerData.put(19, (byte)0x09); // second byte of Major
        mManufacturerData.put(20, (byte)0x00); // first minor
        mManufacturerData.put(21, (byte)0x06); // second minor
        mManufacturerData.put(22, (byte)0xB5); // txPower
        mBuilder.addManufacturerData(224, mManufacturerData.array()); // using google's company ID
        mAdvertiseData = mBuilder.build();
    }

    public byte[] getIdAsByte(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    class MyAdvertiseCallback extends AdvertiseCallback {
        /**
         * Callback triggered in response to {@link BluetoothLeAdvertiser#startAdvertising} indicating
         * that the advertising has been started successfully.
         *
         * @param settingsInEffect The actual settings used for advertising, which may be different from
         *            what has been requested.
         */
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
        }

        /**
         * Callback when advertising could not be started.
         *
         * @param errorCode Error code (see ADVERTISE_FAILED_* constants) for advertising start
         *            failures.
         */
        @Override
        public void onStartFailure(int errorCode) {
        }
    }
}
