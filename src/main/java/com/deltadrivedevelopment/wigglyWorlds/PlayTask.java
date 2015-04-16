package com.deltadrivedevelopment.wigglyWorlds;

import java.io.File;
import java.io.IOException;

import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.util.io.file.FilenameException;

@SuppressWarnings("deprecation")
public class PlayTask extends BukkitRunnable {

	private Animation anim;
	private int counter = 0;
	private int frames;
	private boolean reverse;
	private String framesDirPath;
	private TerrainManager tm;

	public PlayTask(int frameCount, String animDirPath, World world,
			boolean reversed, Animation anim) {
		
		this.anim = anim;
		this.frames = frameCount;
		this.reverse = reversed;
		if (reversed) {
			counter = frames - 1;
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
				anim.setPlaying(false);
				this.cancel();
			}
	}

}
