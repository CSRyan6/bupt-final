package server;

import java.util.ArrayList;

public class host extends node{
    public String ip;
    public String mac;
    public int gpu=1000;
    public int cpu=1000;
    public int memory=1000;
    public ArrayList<task> tlink = new ArrayList<>();

    public host(){}

    public host(int g,int c,int m)
    {
        gpu = g;
        cpu = c;
        memory = m;
    }

    public boolean hold(task t)
    {
        return gpu >= t.gpu && cpu >= t.cpu && memory >= t.memory;
    }

    public boolean assign(task t)
    {
        if(gpu >= t.gpu && cpu >= t.cpu && memory >= t.memory)
        {
            gpu -= t.gpu;
            cpu -= t.cpu;
            memory -= t.memory;
            tlink.add(t);
            return true;
        }
        return  false;
    }
}
