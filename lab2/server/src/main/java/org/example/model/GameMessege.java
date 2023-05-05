package org.example.model;

import java.io.Serializable;
import java.util.ArrayList;

public class GameMessege implements Serializable
{
    public ArrayList<GameObject> objects;
    public ArrayList<PlayerData> playerData;
    public GameMessege(ArrayList<GameObject> objects, ArrayList<PlayerData> playerData)
    {
        this.objects = new ArrayList<GameObject>(objects);
        this.playerData = new ArrayList<PlayerData>(playerData);
    }
}
