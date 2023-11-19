package com.example.lab3korbachdmytro.Service;

import com.example.lab3korbachdmytro.ShowResult.GameResult;

import java.util.ArrayList;

public interface IConnectorObserver {
    public void updateEventObserver(GameResult gameResult);
    public void onConnectObserver(ArrayList<GameResult> objOnCreate);
    public void onReplaceDataObserver(ArrayList<GameResult> objOnCreate);
    public void onDisconnectObserver();
}
