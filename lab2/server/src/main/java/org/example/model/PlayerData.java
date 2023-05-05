package org.example.model;

import java.io.Serializable;

public class PlayerData implements Serializable
{
    public String name;
    public int hits = 0;
    public int scoreRes = 0;
    public PlayerData(String _name)
    {
        this.name = _name;
    }
}
