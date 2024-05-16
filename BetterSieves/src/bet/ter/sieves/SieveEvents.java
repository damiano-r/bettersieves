package bet.ter.sieves;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SieveEvents implements Listener {

    private Sieve plugin;
    public SieveEvents(Sieve plugin) { this.plugin = plugin; }

    public static RandomCollection<String> stoneSieve = new RandomCollection<>();
    public static RandomCollection<String> sandSieve = new RandomCollection<>();
    private String prefix = "§8[§7§lBetter§4§lSieves§8]";

    BukkitTask taskStone;
    BukkitTask taskNether;

    @EventHandler
    public void onItemSent(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();

        Player p = (Player)e.getWhoClicked();
        if (item != null) {

            Inventory dropper = e.getInventory();
            Location l = dropper.getLocation();
            Location l2 = new Location(l.getWorld(), l.getX(), l.getY() + 1.0D, l.getZ());
            Block above = l2.getBlock();
            ItemStack stone = new ItemStack(Material.STONE, 1);             // item di riferimento per la pietra
            ItemStack redsand = new ItemStack(Material.SAND, 1, (short) 1); //item di riferimento per la sabbia rossa

            if (dropper.getName().contains("Sieve")) {  //se il dropper è un Setacciatore
                if (above.getType().equals(Material.CHEST)){  //controlla se c'è la chest sopra il setacciatore, se non c'è.. NON VA AVANTI e viene mandato il msg al player
                    if (dropper.getName().contains("Stone")) {  // se è il Setacciatore della Stone
                        taskStone = new BukkitRunnable() {  //si avvia il task
                            public void run() {
                                for (int i = 0; i < dropper.getSize(); i++) {  //per ogni slot del dropper (9)
                                    ItemStack is = dropper.getItem(i);  // prende l'item nello slot
                                    if (is != null) {  // se non è null o air
                                        if (is.getData().equals(stone.getData())) {  //se l'item è Stone pura.. Granite ecc hanno un DATA diverso da quello della Stone normale (0) e controlla appunto questo
                                            if (is.getAmount() >= 4) {  //controllo se ci sono almeno 4 pezzi di stone all'interno, SENNO NON FUNZIONA, variabile possibile da settare nel config, prossimamente, magari il VIP pagando ne processa di piu ecc ecc, anche se basta spammare con il click... da risolvere lo spam
                                                for (int k = 0; k < 4; k++) { 
                                                    Chest chest = (Chest) above.getState();
                                                    Inventory inv = chest.getInventory();

                                                    String randMaterial = stoneSieve.next(); //prendo l'output come Stringa e lo processo, per vedere se è granite ecc, altrimenti, nell'else puo essere solo GRAVEL e non serve modificare un cazzo e la aggiungo direttamente
                                                    if (randMaterial.contains("_")) {
                                                        String[] split = randMaterial.split("_"); // vai a vedere Sieve.java per capire
                                                        if (split[0].equalsIgnoreCase("STONE")) {   // caso in cui STONE_1, STONE_3, STONE_5 e quindi DIORITE, GRANITE, ANDESITE
                                                            inv.addItem(new ItemStack(Material.getMaterial(split[0]), 1, Short.parseShort(split[1])));
                                                        } else if (split[0].equalsIgnoreCase("SAND")) { // caso in cui sia RED_SAND (SAND_1)
                                                            inv.addItem(new ItemStack(Material.getMaterial(split[0]), 1, Short.parseShort(split[1])));
                                                        }
                                                    } else //il caso in cui sia gravel o sand
                                                        inv.addItem(new ItemStack(Material.getMaterial(randMaterial)));

                                                    is.setAmount(is.getAmount() - 1); //viene aggiornato il valore dello stack, non ho messo 4 perche ho pensato che si puo rendere dinamico mettendolo dal config il numero di item da processare
                                                }
                                                dropper.setItem(i, is); //il tuo setacciatore prendeva tutto lo stack o GLI STACK, li setacciava tutti e rip, qua invece NO, quattro pezzi alla volta
                                                if (is.getAmount() < 4)
                                                    taskStone.cancel();
                                                break; //esco dal ciclo for(i) perche altrimenti per ogni slot me ne processerebbe 4 e non è molto stonks, cioe si ma no
                                            } else
                                                taskStone.cancel();  //se ci sono meno di quattro pezzi nel dropper viene cancellato il task, pero non calcola tutti gli slot, sarebbe da aggiungere, esempio: nello slot 1 ci sono 2 di stone e nello slot 2 ci sono 3 di stone, insieme fanno 5 ma comunque non vengono processati perche in un singolo slot ce ne devono essere piu di 4
                                        } else p.sendMessage(prefix + " §cBlocco non valido, setaccia solo pietra!"); // caso in cui si mette altro nel setacciatore avverte il player con molta gentilezza, spammandoglielo ogni volta che lui clicca :^)
                                    }

                                }
                            }

                        }.runTaskLater(plugin, 20L); //esegue il task dopo un secondo, per debug ho messo un secondo, poi si aggiusta
                    } else if (dropper.getName().contains("Nether")) { //stessa cosa di sopra senza sbatti per processare la stringa, visto che sono tutti item unici
                        taskNether = new BukkitRunnable() {
                            public void run() {
                                for (int i = 0; i < dropper.getSize(); i++) {
                                    ItemStack is = dropper.getItem(i);
                                    if (is != null) {
                                        if (is.getData().equals(redsand.getData())) {
                                            if (is.getAmount() >= 4) {
                                                for (int k = 0; k < 4; k++) {
                                                    Chest chest = (Chest) above.getState();
                                                    Inventory inv = chest.getInventory();

                                                    String randMaterial = sandSieve.next();
                                                    inv.addItem(new ItemStack(Material.getMaterial(randMaterial)));
                                                    is.setAmount(is.getAmount() - 1);
                                                }
                                                dropper.setItem(i, is);
                                                if (is.getAmount() < 4)
                                                    taskNether.cancel();
                                                break;
                                            } else
                                                taskNether.cancel();
                                        } else p.sendMessage(prefix + " §cBlocco non valido, setaccia solo sabbia rossa!");
                                    }
                                }
                            }
                        }.runTaskLater(plugin, 20L);
                    }
                } else p.sendMessage(prefix + " §cPlace a chest on top of the sieve!");
            }
        }
    }

    /*
    @EventHandler
    public void onItemMove(InventoryMoveItemEvent e) {
        Inventory destination = e.getDestination();
        if (destination.getName().equals("Sieve")) {
            Location l = destination.getLocation();
            Location l2 = new Location(l.getWorld(), l.getX(), l.getY() + 1.0D, l.getZ());
            Block above = l2.getBlock();
            ItemStack redsand = new ItemStack(Material.SAND, 1, (short)1);
            if (above.getType().equals(Material.CHEST)) {
                ItemStack item = e.getItem();
                if (item.getType().equals(Material.STONE)) {
                    for (int i = 0; i < destination.getSize() - 1; i++) {
                        ItemStack is = destination.getItem(i);
                        if (is.getType().equals(Material.STONE) && is.getType() != null && !is.getType().equals(Material.AIR) && is != null) {
                            destination.removeItem(new ItemStack(Material.STONE, is.getAmount()));
                            Chest chest = (Chest)above.getState();
                            Inventory inv = chest.getInventory();

                            String randMaterial = stoneSieve.next();
                            if(randMaterial.contains("_")){
                                String[] split = randMaterial.split("_");
                                if(split[0].equalsIgnoreCase("STONE")){
                                    inv.addItem(new ItemStack[]{new ItemStack(Material.getMaterial(split[0]), 1, Short.parseShort(split[1]))});
                                } else if(split[0].equalsIgnoreCase("SAND")){
                                    inv.addItem(new ItemStack[]{new ItemStack(Material.getMaterial(split[0]), 1, Short.parseShort(split[1]))});
                                }
                            } else
                                inv.addItem(new ItemStack[]{new ItemStack(Material.getMaterial(randMaterial))});
                        }
                    }
                }
                else if(item.getType().equals(redsand.getType())){
                    for (int i = 0; i < destination.getSize() - 1; i++) {
                        ItemStack is = destination.getItem(i);
                        if (is.getType() != null && is.equals(redsand) && !is.getType().equals(Material.AIR) && is != null) {
                            destination.removeItem(redsand);

                            Chest chest = (Chest)above.getState();
                            Inventory inv = chest.getInventory();
                            inv.addItem(new ItemStack[]{new ItemStack(Material.getMaterial(sandSieve.next()))});
                        }
                    }
                }
            }
        }
    }
     */

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Block b = e.getBlock();
        if (b.getType().equals(Material.DROPPER)) {
            File f = Utilities.getFile("setacciatori.yml");
            YamlConfiguration setacciatori = YamlConfiguration.loadConfiguration(f);
            List<String> lista1 = setacciatori.getStringList("Setacciatori.StoneSieve");
            int x = b.getX();
            int y = b.getY();
            int z = b.getZ();
            for (int i = 0; i < lista1.size(); i++) {
                String[] split = (lista1.get(i)).split("/");
                if (Integer.parseInt(split[0]) == x && Integer.parseInt(split[1]) == y && Integer.parseInt(split[2]) == z) {
                    e.setDropItems(false);
                    ItemStack is = new ItemStack(Material.DROPPER);
                    ItemMeta isMeta = is.getItemMeta();
                    isMeta.setDisplayName("Sieve");
                    is.setItemMeta(isMeta);
                    Bukkit.getWorld(b.getWorld().getName()).dropItem(b.getLocation(), is);
                    lista1.remove(i);
                    setacciatori.set("Setacciatori.StoneSieve", lista1);
                    break;
                }
            }
            List<String> lista2 = setacciatori.getStringList("Setacciatori.NetherSieve");
            for (int i = 0; i < lista2.size(); i++) {
                String[] split = lista2.get(i).split("/");
                if (Integer.parseInt(split[0]) == x && Integer.parseInt(split[1]) == y && Integer.parseInt(split[2]) == z) {
                    e.setDropItems(false);
                    ItemStack is = new ItemStack(Material.DROPPER);
                    ItemMeta isMeta = is.getItemMeta();
                    isMeta.setDisplayName("Sieve");
                    is.setItemMeta(isMeta);
                    Bukkit.getWorld(b.getWorld().getName()).dropItem(b.getLocation(), is);

                    try {
                        lista2.remove(i);
                        setacciatori.set("Setacciatori.NetherSieve", lista2);
                        break;
                    } catch (Exception ex) {
                        e.getPlayer().sendMessage(String.valueOf(ex));

                        break;
                    }
                }
            }
            try {
                setacciatori.save(f);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        else if(b.getType().equals(Material.WALL_SIGN))
            e.setCancelled(true);
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        Block block = e.getBlock();
        ItemStack itemBlock = e.getItemInHand();

        if (block.getType().equals(Material.DROPPER)) {

            Block placed = e.getBlockPlaced();
            BlockFace targetFace = ((org.bukkit.material.Dispenser) placed.getState().getData()).getFacing();
            double x = placed.getX(); double y = placed.getY(); double z = placed.getZ();

            if(targetFace.equals(BlockFace.NORTH))
                z -= 1;
            else if(targetFace.equals(BlockFace.EAST))
                x += 1;
            else if(targetFace.equals(BlockFace.SOUTH))
                z += 1;
            else if(targetFace.equals(BlockFace.WEST))
                x -= 1;

            Location signLocation = new Location(p.getWorld(), x, y, z);

            p.getWorld().getBlockAt(signLocation).setType(Material.WALL_SIGN);
            Block signBlock = p.getWorld().getBlockAt(signLocation);

            org.bukkit.material.Sign matSign = new org.bukkit.material.Sign(Material.WALL_SIGN);
            matSign.setFacingDirection(targetFace);

            signBlock.getState().setData(new MaterialData(Material.WALL_SIGN));
            Sign sign = (Sign)signBlock.getState();
            sign.setLine(1, ChatColor.translateAlternateColorCodes('&', "&7[&b&lSETACCIATORE&7]"));
            sign.setLine(2, ChatColor.translateAlternateColorCodes('&', "&4&l>> &c&lON &4&l<<"));
            sign.setData(matSign);
            sign.update();

            File f = Utilities.getFile("setacciatori.yml");
            YamlConfiguration setacciatori = YamlConfiguration.loadConfiguration(f);
            if (itemBlock.getItemMeta().getDisplayName().contains("Stone")) {
                List<String> lista = setacciatori.getStringList("Setacciatori.StoneSieve");
                lista.add(placed.getX() + "/" + placed.getY() + "/" + placed.getZ());
                setacciatori.set("Setacciatori.StoneSieve", lista);
            } else {
                List<String> lista = setacciatori.getStringList("Setacciatori.NetherSieve");
                lista.add(placed.getX() + "/" + placed.getY() + "/" + placed.getZ());
                setacciatori.set("Setacciatori.NetherSieve", lista);
            }

            try {
                setacciatori.save(f);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
