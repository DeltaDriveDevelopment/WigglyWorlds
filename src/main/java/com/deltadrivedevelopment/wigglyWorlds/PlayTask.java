package com.deltadrivedevelopment.wigglyWorlds;

import java.io.File;
import java.io.IOException;

import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.FilenameException;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.data.DataException;

public class PlayTask extends BukkitRunnable {

	private int counter = 0;
	private int frames;
	private boolean reverse;
	private String framesDirPath;
	private TerrainManager tm;

	public PlayTask(int frameCount, String animDirPath, World world,
			boolean reversed) {
		this.frames = frameCount;
		this.reverse = reversed;
		if (reversed) {
			counter = frames;
			frames = 0;
		}
		this.framesDirPath = animDirPath;
		tm = new TerrainManager(WigglyWorlds.getWep(), world);
	}

	@Override
	public void run() {
		boolean test;
		
		if(reverse){
			test = counter >= frames;
		} else {
			test = counter < frames;
		}
		
		
		
			if (test) {
				File frame = new File(framesDirPath + File.separator + counter);
				try {
					tm.loadSchematic(frame);
				} catch (FilenameException | DataException
						| MaxChangedBlocksException | EmptyClipboardException
						| IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(!reverse){
					counter++;
				} else if(reverse){
					counter--;
				}
			} else {
				this.cancel();
			}
	}

}
