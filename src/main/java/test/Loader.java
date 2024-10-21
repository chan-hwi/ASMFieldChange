package test;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class Loader {
    public static void main(String[] args) {
        try {
            URLClassLoader loader = URLClassLoader.newInstance(new URL[]{new URL("file:/D:/GraduateProject/JPatchInstFieldChange/build/classes/java/main/")});
            Class<?> clazz = Class.forName("test.Adder", true, loader);
            System.out.println(clazz);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
