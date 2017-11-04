package me.redraskal.survivethenight.utils;

/**
 * Resource from https://github.com/JBoss925/CuboidsAPI/blob/master/src/me/JBoss925/cuboids/Cuboid.java
 */
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;


public class Cuboid implements Iterable<Block> {

    @Getter @Setter private World world;
    @Getter @Setter private Location pos1;
    @Getter @Setter private Location pos2;

    @Getter @Setter private int xmax;
    @Getter @Setter private int xmin;
    @Getter @Setter private int ymax;
    @Getter @Setter private int ymin;
    @Getter @Setter private int zmax;
    @Getter @Setter private int zmin;

    public Cuboid(Location pos1, Location pos2) {
        this.pos1 = pos1;
        this.pos2 = pos2;

        if(pos1.getWorld().getName().equals(pos2.getWorld().getName())){
            this.world = pos1.getWorld();
            this.xmax = Math.max(pos1.getBlockX(), pos2.getBlockX());
            this.xmin = Math.min(pos1.getBlockX(), pos2.getBlockX());
            this.ymax = Math.max(pos1.getBlockY(), pos2.getBlockY());
            this.ymin = Math.min(pos1.getBlockY(), pos2.getBlockY());
            this.zmax = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
            this.zmin = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        }
    }

    public Cuboid(int xmax, int xmin, int ymax, int ymin, int zmax, int zmin, World world) {
        this.world = world;
        this.xmax = xmax;
        this.xmin = xmin;
        this.ymax = ymax;
        this.ymin = ymin;
        this.zmax = zmax;
        this.zmin = zmin;
    }

    @Override
    public Iterator<Block> iterator() {
        return new CuboidIterator(new Cuboid(xmax, xmin, ymax, ymin, zmax, zmin, world));
    }

    public boolean hasPlayerInside(Player player) {
        Location loc = player.getLocation();
        if(xmin <= loc.getX() && xmax >= loc.getX() && ymin <= loc.getY() && ymax >= loc.getY() && zmin <= loc.getZ() && zmax >= loc.getX() && world.getName().equals(loc.getWorld().getName())){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean hasBlockInside(Block block) {
        Location loc = block.getLocation();
        if(xmin <= loc.getX() && xmax >= loc.getX() && ymin <= loc.getY() && ymax >= loc.getY() && zmin <= loc.getZ() && zmax >= loc.getX() && world.getName().equals(loc.getWorld().getName())){
            return true;
        }
        else{
            return false;
        }
    }

    public class CuboidIterator implements Iterator<Block> {

        Cuboid cci;
        World wci;
        int baseX;
        int baseY;
        int baseZ;
        int sizeX;
        int sizeY;
        int sizeZ;
        private int x, y, z;
        ArrayList<Block> blocks;
        Map<Location, Material> blocks2;
        ArrayList<Location> blocks3;

        public CuboidIterator(Cuboid c) {
            this.cci = c;
            this.wci = c.getWorld();
            baseX = getXmin();
            baseY = getYmin();
            baseZ = getZmin();
            sizeX = Math.abs(getXmax() - getXmin()) + 1;
            sizeY = Math.abs(getYmax() - getYmin()) + 1;
            sizeZ = Math.abs(getZmax() - getZmin()) + 1;
            x = y = z = 0;
        }

        public Cuboid getCuboid() {
            return cci;
        }

        public boolean hasNext() {
            return x < sizeX && y < sizeY && z < sizeZ;
        }

        public Block next() {
            Block b = world.getBlockAt(baseX + x, baseY + y, baseZ + z);
            if (++x >= sizeX) {
                x = 0;
                if (++y >= sizeY) {
                    y = 0;
                    ++z;
                }
            }
            return b;
        }

        public void remove() {}

        public Map<Location, Material> getBlockAtLocations() {
            blocks2 = new HashMap<Location, Material>();
            for(int x = cci.getXmin(); x <= cci.getXmax(); x++){
                for(int y = cci.getYmin(); y <= cci.getYmax(); y++){
                    for(int z = cci.getZmin(); z <= cci.getZmax(); z++){
                        blocks2.put(new Location(cci.getWorld(), x, y, z), getWorld().getBlockAt(x, y, z).getType());
                    }
                }
            }
            return blocks2;
        }

        public Collection<Location> getLocations() {
            blocks3 = new ArrayList<Location>();
            for(int x = cci.getXmin(); x <= cci.getXmax(); x++){
                for(int y = cci.getYmin(); y <= cci.getYmax(); y++){
                    for(int z = cci.getZmin(); z <= cci.getZmax(); z++){
                        blocks3.add(new Location(wci, x, y, z));
                    }
                }
            }
            return blocks3;
        }

        public Collection<Block> iterateBlocks() {
            blocks = new ArrayList<Block>();
            for(int x = cci.getXmin(); x <= cci.getXmax(); x++){
                for(int y = cci.getYmin(); y <= cci.getYmax(); y++){
                    for(int z = cci.getZmin(); z <= cci.getZmax(); z++){
                        blocks.add(cci.getWorld().getBlockAt(x, y, z));
                    }
                }
            }
            return blocks;
        }
    }
}