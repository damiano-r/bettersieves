package bet.ter.sieves;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

public class Main extends JavaPlugin {
    public void onEnable() {
        instance = this;
        loadConfiguration();
        getServer().getPluginManager().registerEvents(new SieveEvents(this), (Plugin)this);
        Utilities.getFile("setacciatori.yml");
        super.onEnable();

        setWeightsOf("StoneSieve", SieveEvents.stoneSieve);
        setWeightsOf("NetherSieve", SieveEvents.sandSieve);

        //getLogger().info(SieveEvents.stoneSieve.toString());

    }

    private static Main instance;

    public void onDisable() { super.onDisable(); }

    public static Main getInstance() { return instance; }

    public void loadConfiguration(){
        this.saveDefaultConfig();
    }

    public void setWeightsOf(String _path, RandomCollection<String> collection){
        String material = "";
        int chance = 0;
        List<Map<?, ?>> a = getConfig().getMapList(_path);
        for (Map<?, ?> map : a) {
            for (Map.Entry<?, ?> e : map.entrySet()) {
                if (e.getKey().equals("Material"))
                    material = (String) e.getValue();
                else
                    chance = (Integer) e.getValue();
            }
            collection.add(chance, material);
        }
    }

}
