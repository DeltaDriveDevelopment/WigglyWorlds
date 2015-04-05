package com.deltadrivedevelopment.wigglyWorlds;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.FilenameException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.data.DataException;

public class PrivatePlayTask extends BukkitRunnable {

	private int counter = 0;
	private int frames;
	private String framesDirPath;
	private TerrainManager tm;
	private Player player;
	private World world;
	
	public PrivatePlayTask(int frameCount, String animDirPath, World world, Player player){
		this.player = player;
		this.frames = frameCount;
		this.framesDirPath = animDirPath;
		this.world = world;
		tm = new TerrainManager(WigglyWorlds.getWep(), world);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		if(counter < frames){
			File frame = new File(framesDirPath + File.separator + counter);
			try {
				LocalSession localSession = tm.getLocalSessionWithClipboard(frame);
				CuboidClipboard clipboard = localSession.getClipboard();
				int x = clipboard.getWidth();
				int y = clipboard.getHeight();
				int z = clipboard.getLength();
				BaseBlock b = null;
				Vector origin = clipboard.getOrigin();
				player.sendMessage("X origin = " + origin.getBlockX());
				player.sendMessage("Y origin = " + origin.getBlockY());
				player.sendMessage("Z origin = " + origin.getBlockZ());
				for(int x1 = 0; x1 < x; x1++){
					for(int y1 = 0; y1 < y; y1++){
						for(int z1 = 0; z1 <  z; z1++){
							Vector position = new Vector(x1, y1, z1);
							Location loc = new Location(world, origin.getBlockX() + x1, origin.getBlockY() + y1, origin.getBlockZ() + z1);
							try{
								b = clipboard.getBlock(position);
							} catch (ArrayIndexOutOfBoundsException e){
								Bukkit.getServer().getLogger().info("Index is out of bounds, offending vector is (" + x1 + ", " + y1 + ", " + z1 + ")");
							}
							
							if(b != null){
								if(loc.getBlock().getTypeId() != b.getId()){
									player.sendBlockChange(loc, b.getId(), (byte)b.getData());
								}
							}
							
						}
					}
				}
				
			} catch (FilenameException | DataException
					| MaxChangedBlocksException | EmptyClipboardException
					| IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			counter++;
		} else {
			this.cancel();
		}
		
	}	

}
