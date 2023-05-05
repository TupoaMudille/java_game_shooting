package org.example.model;

import java.io.Serializable;

public abstract class GameObject implements Serializable
{
    public int x;
    public int y;
    public String name;
    public GameObjectType type;
    protected MoveStatus status;
    public abstract void Move();
    public abstract void Action();
    public GameObject(int _x, int _y, String _name)
    {
        this.x=_x;
        this.y=_y;
        this.name=_name;
    }
    public boolean IsTarget()
    {
        if (this.type == GameObjectType.TARGET)
        {
            return this.status != MoveStatus.MOVE_STOP;
        }
        return false;
    }
    public boolean CheckCollision(GameObject _object) throws Exception
    {
        if (_object==null)
        {
            throw new Exception("Объект не существует");
        }
        if (this.x <= _object.x+50 && this.x >= _object.x)
        {
            return this.y <= _object.y+50 && this.y >= (_object.y);
        }
        return false;
    }
    public boolean IsPlayer()
    {
        return this.type == GameObjectType.PLAYER;
    }
    public void Stop()
    {
        return;
    }

}
