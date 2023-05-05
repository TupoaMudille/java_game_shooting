package org.example.model;

public class Target extends GameObject
{
    @Override
    public boolean CheckCollision(GameObject _object) throws Exception
    {
        boolean superResult = super.CheckCollision(_object);
        if (superResult)
        {
            this.status=MoveStatus.MOVE_STOP;
        }
        return superResult;
    }

    @Override
    public void Move()
    {
        switch (this.status)
        {
            case MOVE_TO_END ->
            {
                this.y = this.y+(this.x/100);
                if (this.y>= new Config().SIZEY)
                {
                    this.status = MoveStatus.MOVE_BACK;
                }
            }
            case MOVE_BACK, MOVE_STOP ->
            {
                this.y = this.y-(this.x/100);
                if (this.y<=0)
                {
                    this.status = MoveStatus.MOVE_TO_END;
                }
            }
        }
    }

    @Override
    public void Action()
    {
        return;
    }

    public Target(int _x, int _y, String _name)
    {
        super(_x, _y, _name);
        this.type = GameObjectType.TARGET;
        this.status = MoveStatus.MOVE_TO_END;
    }
}
