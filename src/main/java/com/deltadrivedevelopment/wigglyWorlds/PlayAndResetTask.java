package com.deltadrivedevelopment.wigglyWorlds;

import java.io.File;
import java.io.IOException;

import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.FilenameException;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.data.DataException;

public class PlayAndResetTask extends BukkitRunnable {

	private int counter = 0;
	private int frames;
	private String framesDirPath;
	private TerrainManager tm;
	
	public PlayAndResetTask(int frameCount, String animDirPath, World world){
		this.frames = frameCount;
		this.framesDirPath = animDirPath;
		tm = new TerrainManager(WigglyWorlds.getWep(), world);
	}
	
	@Override
	public void run() {
		if(counter < frames){
			File frame = new File(framesDirPath + File.separator + counter);
			try {
				tm.loadSchematic(frame);
			} catch (FilenameException | DataException
					| MaxChangedBlocksException | EmptyClipboardException
					| IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			counter++;
		} else {
			try {
				tm.loadSchematic(new File(framesDirPath + File.separator + 0));
			} catch (FilenameException | DataException
					| MaxChangedBlocksException | EmptyClipboardException
					| IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			this.cancel();
		}
		
	}	

}
