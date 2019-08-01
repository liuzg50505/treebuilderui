package com.liuzg.flutteride.def;


import java.io.*;

public class DefUtils {
    public static ConstructorInstance decorate(ConstructorDefinition decoratorConstructorDefinition, Instance target) {
        ConstructorInstance constructorInstance = new ConstructorInstance(decoratorConstructorDefinition);
        constructorInstance.setProperty(decoratorConstructorDefinition.decoratorProperty, target);
        return constructorInstance;
    }

    public static  <T> T getCopyObj(T t) {

        ByteArrayOutputStream byteArrayOutputStream = null;
        ObjectOutputStream objectOutputStream = null;

        ByteArrayInputStream byteArrayInputStream = null;
        ObjectInputStream objectInputStream = null;

        try {
            byteArrayOutputStream = new ByteArrayOutputStream();

            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(t);//序列化对象
            objectOutputStream.flush();

            byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            T t1 = (T) objectInputStream.readObject();
            return t1;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                    byteArrayOutputStream = null;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                    objectOutputStream = null;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (byteArrayInputStream != null) {
                try {
                    byteArrayInputStream.close();
                    byteArrayInputStream = null;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (objectInputStream != null) {
                try {
                    objectInputStream.close();
                    objectInputStream = null;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return null;

    }

}
