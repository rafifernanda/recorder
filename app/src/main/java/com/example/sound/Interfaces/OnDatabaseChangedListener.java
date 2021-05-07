package com.example.sound.Interfaces;

import com.example.sound.Models.RecordingItem;

public interface OnDatabaseChangedListener
{
    void onNewDatabaseEntryAdded(RecordingItem recordingItem);

}
