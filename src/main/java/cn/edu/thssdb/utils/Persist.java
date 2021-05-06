package cn.edu.thssdb.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Persist {
    public static void serialize(String path, Iterator<Object> iterator){
        File file = new File(path);
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
            while (iterator.hasNext()){
                Object object = iterator.next();
                outputStream.writeObject(object);
            }
            outputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> ArrayList<T> deserialize(String path){
        File file = new File(path);
        if(!file.exists()){
            return new ArrayList<T>();
        }
        ArrayList<T> objects = new ArrayList<>();
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
            while (true){
                try {
                    T object = (T) inputStream.readObject();
                    objects.add(object);
                } catch (EOFException exception){
                    break;
                }
            }
            inputStream.close();
            fileInputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return objects;
    }
}
