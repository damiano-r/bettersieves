package bet.ter.sieves;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Sieve extends JavaPlugin {
    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(new SieveEvents(this), (Plugin)this);
        Utilities.getFile("setacciatori.yml");
        super.onEnable();

        //inizializzo i valori qui, NON COME LO HAI FATTO TU OGNI VOLTA CHE SERVIVA MADO CHE CASINO
        SieveEvents.stoneSieve.add(50.0D, "GRAVEL");
        SieveEvents.stoneSieve.add(20.0D, "SAND");
        SieveEvents.stoneSieve.add(10.0D, "SAND_1");  // RED_SAND
        SieveEvents.stoneSieve.add(10.0D, "STONE_3"); // DIORITE
        SieveEvents.stoneSieve.add(5.0D, "STONE_5");  // ANDESITE
        SieveEvents.stoneSieve.add(5.0D, "STONE_1");  // GRANITE

        SieveEvents.sandSieve.add(50.0D, "GLOWSTONE");
        SieveEvents.sandSieve.add(20.0D, "NETHERRACK");
        SieveEvents.sandSieve.add(10.0D, "SOUL_SAND");
        SieveEvents.sandSieve.add(10.0D, "NETHER_BRICK_ITEM");
        SieveEvents.sandSieve.add(5.0D, "RED_MUSHROOM");
        SieveEvents.sandSieve.add(5.0D, "NETHER_STALK");
    }

    private static Sieve instance;

    public void onDisable() { super.onDisable(); }

    public static Sieve getInstance() { return instance; }

    public static Runnable processStone = new Runnable(){
        @Override
        public void run() {

        }
    };
}
