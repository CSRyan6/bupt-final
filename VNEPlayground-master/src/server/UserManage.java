package server;

import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;

public class UserManage {
    public static ArrayList<Pair<String,String>> readFile(String fileName)
    {
        File file = new File(fileName);
        BufferedReader reader = null;
        ArrayList<Pair<String,String>> users = new ArrayList<>();
        try
        {
            reader = new BufferedReader(new FileReader(file));
            String temps;
            while ((temps = reader.readLine()) != null)
            {

                String[] infs = temps.split(" ");
//                for(int i=0;i<infs.length;i++)
//                    System.out.println(infs[i]);
                Pair<String,String> user = new Pair<>(infs[0],infs[1]);
                users.add(user);
//                System.out.println(temps);
            }
            reader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        }
        return users;
    }

    public static boolean writeFile(ArrayList<Pair<String,String>> users,String username,String password,String fileName){
        for(int i = 0;i < users.size();i++)
        {
            if(users.get(i).getKey().equals(username))
                return false;
        }
        String tempLine = username + " " + password;
        try
        {
            FileWriter writer = new FileWriter(fileName ,true);
            writer.write(System.getProperty("line.separator"));
            writer.write(tempLine);
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
