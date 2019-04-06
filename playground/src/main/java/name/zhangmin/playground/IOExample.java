package name.zhangmin.playground;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class IOExample {
    public static void main(String[] args) {
        try {
            FileInputStream fis = new FileInputStream("StringExample");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
