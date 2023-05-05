package org.example;

import org.example.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Semaphore;

public class Game
{
    private ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
    private ArrayList<PlayerData> playerDataArrayList = new ArrayList<PlayerData>();
    private Semaphore gameObjectsSemaphore = new Semaphore(1);
    private Semaphore playerDataSemaphore = new Semaphore(1);
    private MainLoop loop = new MainLoop();
    public Game()
    {
        gameObjects.add(new Target(500, 200, "Target1"));
        gameObjects.add(new Target(400, 200, "Target2"));
    }
    public void RemovePlayer(String _name)
    {
        try
        {
            gameObjectsSemaphore.acquire();
            gameObjects.remove(GetPlayerObjectByName(gameObjects, _name));
            int j = 1;
            for (int i = 0; i<gameObjects.size(); i++)
            {
                if (gameObjects.get(i).type == GameObjectType.TARGET)
                {
                    continue;
                }
                gameObjects.get(i).y=100*j;
                j++;
            }
            gameObjectsSemaphore.release();
            playerDataSemaphore.acquire();
            playerDataArrayList.remove(GetPlayerDataByName(playerDataArrayList, _name));
            playerDataSemaphore.release();
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }
    public PlayerData GetPlayerDataByName(String _name)
    {
        PlayerData result;
        try
        {
            playerDataSemaphore.acquire();
            result = GetPlayerDataByName(playerDataArrayList, _name);
            playerDataSemaphore.release();
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
        return result;
    }

    private PlayerData GetPlayerDataByName(List<PlayerData> _list, String _name)
    {
        for (int i = 0; i<_list.size(); i++)
        {
            if (Objects.equals(_list.get(i).name, _name))
            {
                return _list.get(i);
            }
        }
        return null;
    }
    private GameObject GetPlayerObjectByName(List<GameObject> _list, String _name)
    {
        for (int i = 0; i<_list.size(); i++)
        {
            if (Objects.equals(_list.get(i).name, _name))
            {
                return _list.get(i);
            }
        }
        return null;
    }
    public void Shoot(String _name)
    {
        try
        {
            gameObjectsSemaphore.acquire();
            GetPlayerObjectByName(gameObjects ,_name).Action();
            gameObjectsSemaphore.release();
            playerDataSemaphore.acquire();
            GetPlayerDataByName(playerDataArrayList, _name).hits++;
            playerDataSemaphore.release();
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }

    }
    public void StartGame()
    {
        loop.start();
    }
    public void AddPlayer(String _name)
    {
        try
        {
            gameObjectsSemaphore.acquire();
            gameObjects.add(new Player(10, 100*(playerDataArrayList.size()+1), _name));
            gameObjectsSemaphore.release();
            playerDataSemaphore.acquire();
            playerDataArrayList.add(new PlayerData(_name));
            playerDataSemaphore.release();
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }
    public GameMessege GetGameStatus()
    {
        try
        {
            gameObjectsSemaphore.acquire();
            ArrayList<GameObject> result = new ArrayList<GameObject>(gameObjects);
            gameObjectsSemaphore.release();
            playerDataSemaphore.acquire();
            ArrayList<PlayerData> resultData = new ArrayList<PlayerData>(playerDataArrayList);
            playerDataSemaphore.release();
            return new GameMessege(result, resultData);
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }
    private class MainLoop extends Thread
    {
        private void MoveAll()
        {
            try
            {
                gameObjectsSemaphore.acquire();
                for (int i = 0; i<gameObjects.size(); i++)
                {
                    gameObjects.get(i).Move();
                }
                gameObjectsSemaphore.release();
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }
        private void CheckCollision()
        {
            try
            {
                gameObjectsSemaphore.acquire();
                for (int i = 0; i<gameObjects.size(); i++)
                {
                    if (gameObjects.get(i).IsPlayer())
                    {
                        continue;
                    }
                    for (int j = 0; j<gameObjects.size(); j++)
                    {
                        if (gameObjects.get(j).type == GameObjectType.TARGET)
                        {
                            continue;
                        }
                        if (gameObjects.get(i).CheckCollision(gameObjects.get(j)))
                        {
                            int tempInt=0;
                            if (Objects.equals(gameObjects.get(i).name, "Target2"))
                            {
                                tempInt=1;

                            } else if (Objects.equals(gameObjects.get(i).name, "Target1")) {
                                tempInt=2;

                            }
                            gameObjects.get(j).Stop();
                            playerDataSemaphore.acquire();
                            GetPlayerDataByName(playerDataArrayList, gameObjects.get(j).name).scoreRes +=tempInt;
                            playerDataSemaphore.release();
                        }
                    }
                }

                gameObjectsSemaphore.release();
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        @Override
        public void run()
        {
            while (true)
            {
                MoveAll();
                CheckCollision();
                try
                {
                    sleep(34);
                }
                catch (InterruptedException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
