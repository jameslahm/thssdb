package cn.edu.thssdb.utils;

import cn.edu.thssdb.schema.Column;
import cn.edu.thssdb.statement.BaseStatement;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

public class Persist {
    public static void serialize(String path, Iterator<Object> iterator){
        File file = getOrCreateFile(path);
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
    public static void serializeLog(String path, Iterator<BaseStatement> iterator){
        deleteFile(path);
        File file = getOrCreateFile(path);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
            while (iterator.hasNext()){
                BaseStatement statement = iterator.next();
                outputStream.writeObject(statement);
            }
            outputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void deleteFile(String path){
        try {
            File file = new File(path);
            if(file.exists()){
                Files.delete(Paths.get(path));
            }
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

    public static ArrayList<String> fromJsonToManagerMeta(String path){
        try {
            byte[] bytes =  Files.readAllBytes(Paths.get(path));
            String json = new String(bytes, StandardCharsets.UTF_8);
            Type array = new TypeToken<ArrayList<String>>(){}.getType();
            Gson gson = new Gson();
            return gson.fromJson(json,array);

        } catch (IOException e) {

        }
        return new ArrayList<>();
    }

    public static void fromManagerMetaToJson(ArrayList<String> databaseNames, String path){
        getOrCreateFile(path);
        Gson gson = new Gson();
        String json = gson.toJson(databaseNames);
        try {
            Files.write(Paths.get(path),json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> fromJsonToDatabaseMeta(String path){
        try {
            byte[] bytes =  Files.readAllBytes(Paths.get(path));
            String json = new String(bytes, StandardCharsets.UTF_8);
            Type array = new TypeToken<ArrayList<String>>(){}.getType();
            Gson gson = new Gson();
            return gson.fromJson(json,array);

        } catch (IOException e) {
        }
        return new ArrayList<>();
    }

    public static void fromDatabaseMetaToJson(ArrayList<String> tableNames, String path){
        getOrCreateFile(path);
        Gson gson = new Gson();
        String json = gson.toJson(tableNames);
        try {
            Files.write(Paths.get(path),json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Column> fromJsonToTableMeta(String path){
        try {
            byte[] bytes =  Files.readAllBytes(Paths.get(path));
            String json = new String(bytes, StandardCharsets.UTF_8);
            Type array = new TypeToken<ArrayList<Column>>(){}.getType();
            Gson gson = new Gson();
            return gson.fromJson(json,array);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static void fromTableMetaToJson(ArrayList<Column> names, String path){
        getOrCreateFile(path);
        Gson gson = new Gson();
        String json = gson.toJson(names);
        try {
            Files.write(Paths.get(path),json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File getOrCreateFile(String path){
        File file = new File(path);
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
