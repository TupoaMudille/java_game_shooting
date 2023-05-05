package org.example;
import org.example.model.Config;
import org.example.model.DatabaseProvider;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Main
{
    private static class Client
    {
        public Socket socket;
        private String name;
        private ListenerThread listenerThread = new ListenerThread();
        public Client(Socket socket, String _name)
        {
            this.name = _name;
            this.socket = socket;
            this.listenerThread.start();
        }
        private class ListenerThread extends Thread
        {
            @Override
            public void run()
            {
                while (true)
                {
                    try
                    {
                        socket.getInputStream().read();
                        game.Shoot(name);
                    }
                    catch (IOException e)
                    {
                        System.out.println("Отвал клиента, Имя: "+name);
                        databaseProvider.UpdateScore(game.GetPlayerDataByName(name));
                        game.RemovePlayer(name);
                        return;
                    }
                }
            }
        }
    }
    private static Game game = new Game();
    private static DatabaseProvider databaseProvider = new DatabaseProvider();
    public static class Server
    {
        private ServerSocket serverSocket = null;
        private ArrayList<Client> clients = new ArrayList<Client>();
        private AccepterThread accepterThread = new AccepterThread();
        private BroadcastThread broadcastThread = new BroadcastThread();
        public Server()
        {
            try
            {
                serverSocket = new ServerSocket(new  Config().PORT, 2, new Config().ip);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }

        }
        public void StartServer()
        {
            accepterThread.start();
            broadcastThread.start();
        }
        private void Broadcast()
        {
            for (int i = 0; i < clients.size(); i++)
            {
                try
                {
                    ObjectOutputStream oos = new ObjectOutputStream(clients.get(i).socket.getOutputStream());
                    oos.writeObject(game.GetGameStatus());
                }
                catch (IOException e)
                {
                    clients.remove(i);
                }
            }
        }
        private class BroadcastThread extends Thread
        {
            @Override
            public void run()
            {
                while (true)
                {
                    Broadcast();
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
        private class AccepterThread extends Thread
        {
            @Override
            public void run()
            {
                while (true)
                {
                    try
                    {
                        Socket temp = serverSocket.accept();
                        String s = (String)new ObjectInputStream(temp.getInputStream()).readObject();
                        game.AddPlayer(s);
                        clients.add(new Client(temp, s));
                        System.out.println("Клиент получен, Имя: "+temp.getPort());
                    }
                    catch (IOException e)
                    {
                        throw new RuntimeException(e);
                    }
                    catch (ClassNotFoundException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
    private static class ScoreServer
    {
        private ServerSocket serverSocket = null;
        private ScoreServerThread scoreServerThread = new ScoreServerThread();
        public ScoreServer()
        {
            try
            {
                serverSocket = new ServerSocket(new  Config().PORT+10, 2, new Config().ip);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
            scoreServerThread.start();
            //System.out.println("Сервер запущен");
        }
        private class ScoreServerThread extends Thread
        {
            @Override
            public void run()
            {
                while (true)
                {
                    try
                    {
                        Socket temp = serverSocket.accept();
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(temp.getOutputStream());
                        objectOutputStream.writeObject(databaseProvider.GetScore(5));
                    }
                    catch (IOException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public static void main(String args[]){
        game.StartGame();
        Server server = new Server();
        server.StartServer();
        ScoreServer scoreServer = new ScoreServer();
    }

    public static void startGameServer()
    {
        game.StartGame();
        Server server = new Server();
        server.StartServer();
        ScoreServer scoreServer = new ScoreServer();
    }

}
