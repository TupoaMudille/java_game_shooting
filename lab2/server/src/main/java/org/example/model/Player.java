package org.example.model;
public class Player extends GameObject
{
    private int startX;

    @Override
    public void Stop() {
        this.x=startX;
        this.status = MoveStatus.MOVE_STOP;
    }

    @Override
    public void Move()
    {
        if (this.status != MoveStatus.MOVE_TO_END)
        {
            return;
        }
        this.x=this.x+25;
        if (this.x > new Config().SIZEX)
        {
            this.x=startX;
            this.status = MoveStatus.MOVE_STOP;
        }

    }
    @Override
    public void Action()
    {
        this.status = MoveStatus.MOVE_TO_END;
    }
    public Player(int _x, int _y, String _name)
    {
        super(_x, _y, _name);
        this.type = GameObjectType.PLAYER;
        this.status=MoveStatus.MOVE_STOP;
        this.startX=_x;
    }
}
