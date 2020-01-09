package bet.ter.sieves;

import java.io.File;
import java.io.IOException;

public class Utilities {
    public static File getFile(String name) { return getFile(null, name); }

    public static File getFile(File parent, String name) {
        if (parent == null) parent = Sieve.getInstance().getDataFolder();
        if (name == null || name.isEmpty()) throw new IllegalArgumentException("File name cannot be null or empty");
        if (!name.toLowerCase().endsWith(".yml")) name = String.valueOf(name) + ".yml";
        File file = new File(parent, name);
        if (!file.exists())
            try { file.createNewFile(); }
            catch (IOException e) { e.printStackTrace(); }

        return file;
    }
}
