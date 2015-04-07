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

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.FilenameException;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;

public class Animation implements Serializable {

	private static final long serialVersionUID = -716774894145227916L;
	public static ArrayList<Animation> animations = new ArrayList<Animation>();

	private LocationPack lp1, lp2;
	private String name;
	private int frameCount;
	private boolean hasFrames;
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
	 * Plays the animation once through, leaving blocks in the state of the
	 * final frame
	 */
	public void play(boolean reversed) {
		new PlayTask(frameCount, animDirPath, lp1.unpack().getWorld(), reversed)
				.runTaskTimer(WigglyWorlds.getP(), 20, 5);
	}

	/**
	 * Plays the animation once through and resets to the 0 frame when complete
	 */
	public void playAndReset(boolean reversed) {
		new PlayAndResetTask(frameCount, animDirPath, lp1.unpack().getWorld(),
				reversed).runTaskTimer(WigglyWorlds.getP(), 20, 5);
	}

	/**
	 * 
	 * @param player
	 *            Player to play the animation for
	 */
	public void playprivate(Player player, boolean reversed) {
		// TODO Auto-generated method stub
		new PrivatePlayTask(frameCount, animDirPath, lp1.unpack()
				.getWorld(), player, reversed).runTaskTimer(
				WigglyWorlds.getP(), 20, 5);
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

	public static Collection<? extends String> getAnimationNames() {
		// TODO Auto-generated method stub

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

}
