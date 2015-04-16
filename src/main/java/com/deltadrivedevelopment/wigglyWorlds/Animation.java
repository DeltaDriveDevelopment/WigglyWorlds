package com.deltadrivedevelopment.wigglyWorlds;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.util.io.file.FilenameException;

@SuppressWarnings("deprecation")
public class Animation implements Serializable {

	private static final long serialVersionUID = -716774894145227916L;
	public static ArrayList<Animation> animations = new ArrayList<Animation>();

	private  transient BukkitTask playerTask;
	private LocationPack lp1, lp2;
	private String name;
	private int frameCount;
	private boolean hasFrames;
	private boolean isPlaying;
	private String animDirPath;
	private File animDir;
	private transient WorldEditPlugin wep = WigglyWorlds.getWep();

	/**
	 * 
	 * @param loc1
	 *            one corner of selection
	 * @param loc2
	 *            Other corner
	 * @param name
	 *            name for new animation
	 * @return a reference to the new animation if successful, null otherwise
	 * 
	 *         used to create new animations and add them to the array of
	 *         animations
	 */
	public static Animation createAnimation(Location loc1, Location loc2,
			String name) {
		Path path = Paths.get(WigglyWorlds.getP().getDataFolder()
				.getAbsolutePath()
				+ File.separator + name);

		if (Files.notExists(path, LinkOption.NOFOLLOW_LINKS)) {
			Animation result = new Animation(loc1, loc2, name);
			animations.add(result);
			result.setFrameCount(-1);
			result.addFrameAt(-1);
			return result;
		} else {
			return null;
		}

	}

	/**
	 * Creates a new copy of the provided animation with the given name
	 * 
	 * @param name
	 * @return
	 */
	public static Animation createAnimation(String toCopyName, String name) {

		return null;
	}

	/**
	 * creates a copy of the provided animation with the given name
	 * 
	 * @param anim
	 * @return
	 */
	public static Animation createAnimation(Animation anim, String name) {

		return null;
	}

	/**
	 * 
	 * @param name
	 * @return the animation if found, null otherwise Gets an animation by its
	 *         name, animation names are case insensitive
	 * 
	 */
	public static Animation getAnimation(String name) {
		if (animations.isEmpty()) {
			return null;
		} else {
			for (Animation anim : animations) {
				if (name.equalsIgnoreCase(anim.getName())) {
					return anim;
				}
			}
			return null;
		}
	}

	/**
	 * 
	 * @param loc1
	 * @param loc2
	 * @param name
	 * 
	 *            Constructor should never be directly called, use
	 *            Animation.createAnimation to properly create an animation
	 */
	public Animation(Location loc1, Location loc2, String name) {
		lp1 = new LocationPack(loc1);
		lp2 = new LocationPack(loc2);
		this.name = name;
		this.frameCount = 0;
		hasFrames = false;

		this.animDirPath = WigglyWorlds.getP().getDataFolder()
				.getAbsolutePath()
				+ File.separator + name;

		animDir = new File(animDirPath);
		// New animations cannot be created if the directory already exists, so
		// no need to test for existence
		animDir.mkdir();
		isPlaying = false;
	}

	/**
	 * Saves the current stage to file for later play back
	 * 
	 * @return
	 */
	public boolean addFrame() {
		TerrainManager tm = new TerrainManager(wep, lp1.unpack().getWorld());
		File newFrame = new File(animDirPath + File.separator + frameCount);
		try {
			tm.saveTerrain(newFrame, lp1.unpack(), lp2.unpack());
			frameCount++;
			if (frameCount > 0) {
				hasFrames = true;
			}
			return true;
		} catch (FilenameException | DataException | IOException e) {
			e.printStackTrace();
			return false;
		}

	}
	
	/**
	 * Adds a frame to the given index
	 * @param index to add frame at
	 * @return True if successful
	 */
	public boolean addFrameAt(int index){
		if(index == frameCount){
			if(addFrame()){
				return true;
			} else {
				return false;
			}
		}
		
		TerrainManager tm = new TerrainManager(wep, lp1.unpack().getWorld());
		
		for(int i = frameCount - 1; i >= index; i--){
			File increment = new File(animDirPath + File.separator + i + ".schematic");
			File temp = new File(animDirPath + File.separator + (i+1) + ".schematic");
			boolean success = increment.renameTo(temp);
			if(!success){
				Bukkit.getServer().getLogger().info("Renaming file " + i + " failed");
				return false;
			}
		}
		
		File newFrame = new File(animDirPath + File.separator + index);
		try {
			tm.saveTerrain(newFrame, lp1.unpack(), lp2.unpack());
			frameCount++;
			return true;
		} catch (FilenameException | DataException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Deletes the last frame in the animation
	 * 
	 * @return True if the frame was deleted
	 */
	public boolean delFrame() {
		File toDelete = new File(animDirPath + File.separator + (frameCount - 1) + ".schematic");
		if(toDelete.exists()){
			if(toDelete.delete()){
				frameCount--;
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	/**
	 * Deletes the frame at the given index and adjusts all other frames accordingly
	 * 
	 * @param index, index of frame to delete
	 * @return true if successful
	 */
	public boolean delFrameAt(int index){
		if(index == frameCount - 1) {
			if(delFrame()){
				return true;
			} else{ 
				return false;
			}
		}
		
		File toDelete = new File(animDirPath + File.separator + index + ".schematic");
		if(toDelete.exists()){
			if(toDelete.delete()){
				frameCount--;
				for(int i = index + 1; i < frameCount + 1; i++){
					File decrement = new File(animDirPath + File.separator + i + ".schematic");
					File temp = new File(animDirPath + File.separator + (i-1) + ".schematic");
					boolean success = decrement.renameTo(temp);
					if(!success){
						Bukkit.getServer().getLogger().info("Renaming file " + i + " failed");
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Plays the animation once through, leaving blocks in the state of the
	 * final frame
	 */
	public void play(boolean reversed, long fps) {
		isPlaying = true;
		playerTask = new PlayTask(frameCount, animDirPath, lp1.unpack().getWorld(), reversed, this)
				.runTaskTimer(WigglyWorlds.getP(), 20, fps);
	}

	/**
	 * Plays the animation once through and resets to the 0 frame when complete
	 */
	public void playAndReset(boolean reversed, long fps) {
		isPlaying = true;
		playerTask = new PlayAndResetTask(frameCount, animDirPath, lp1.unpack().getWorld(),
				reversed, this).runTaskTimer(WigglyWorlds.getP(), 20, fps);
	}

	/**
	 * 
	 * @param player
	 *            Player to play the animation for
	 */
	public void playprivate(Player player, boolean reversed, long fps) {
		// TODO Auto-generated method stub
		new PrivatePlayTask(frameCount, animDirPath, lp1.unpack()
				.getWorld(), player, reversed).runTaskTimer(
				WigglyWorlds.getP(), 20, fps);
	}

	/**
	 * Plays the animation on a loop until stopped
	 * @param reversed
	 * @param fps
	 */
	public void playLoop(boolean reversed, long fps, boolean toggleRev){
		isPlaying = true;
		playerTask = new LoopPlayTask(frameCount, animDirPath, lp1.unpack().getWorld(), reversed, this, toggleRev).runTaskTimer(WigglyWorlds.getP(), 20, fps);
	}
	/**
	 * resets stage to 0 frame
	 */
	public void reset() {
		new BukkitRunnable() {
			File frame = new File(animDirPath + File.separator + "0");
			TerrainManager tm = new TerrainManager(WigglyWorlds.getWep(), lp1
					.unpack().getWorld());

			@Override
			public void run() {
				try {
					tm.loadSchematic(frame);
				} catch (FilenameException | DataException
						| MaxChangedBlocksException | EmptyClipboardException
						| IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.runTaskLater(WigglyWorlds.getP(), 20);
	}

	public String getName() {
		return name;
	}

	public String getAnimDirPath() {
		return animDirPath;
	}

	public File getAnimDir() {
		return animDir;
	}

	public boolean hasFrames() {
		return hasFrames;
	}
	
	public int getFrameCount(){
		return frameCount;
	}
	
	public void setFrameCount(int frameCount){
		this.frameCount = frameCount;
	}
	
	public LocationPack getMinimumPoint(){
		return lp1;
	}
	
	public LocationPack getMaximumPoint(){
		return lp2;
	}

	public boolean stopPlaying(){
		if(isPlaying){
			if(playerTask != null){
				playerTask.cancel();
				playerTask = null;
				isPlaying = false;
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public boolean isPlaying(){
		return isPlaying;
	}
	
	public BukkitTask getPlayerTask(){
		return playerTask;
	}
	
	public void setPlaying(Boolean playing){
		isPlaying = playing;
	}

	public static Collection<? extends String> getAnimationNames() {
		List<String> result = new ArrayList<>();
		
		if (animations.isEmpty()) {
			result.add("");
		} else {
			for (Animation anim : animations) {
				result.add(anim.getName().toLowerCase());
			}
		}
		return result;
	}

	public void loadFrame(final int i) {
		new BukkitRunnable() {
			File frame = new File(animDirPath + File.separator + i);
			TerrainManager tm = new TerrainManager(WigglyWorlds.getWep(), lp1
					.unpack().getWorld());

			@Override
			public void run() {
				try {
					tm.loadSchematic(frame);
				} catch (FilenameException | DataException
						| MaxChangedBlocksException | EmptyClipboardException
						| IOException e) {
					e.printStackTrace();
				}
			}
		}.runTaskLater(WigglyWorlds.getP(), 20);
		
	}

}
