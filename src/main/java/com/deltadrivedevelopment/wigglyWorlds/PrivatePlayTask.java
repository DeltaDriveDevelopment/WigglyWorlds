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
import com.sk89q.worldedit.util.io.file.FilenameException;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.data.DataException;

@SuppressWarnings("deprecation")
public class PrivatePlayTask extends BukkitRunnable {

	private int counter = 0;
	private int frames;
	private String framesDirPath;
	@SuppressWarnings("unused")
	private TerrainManager tm;
	private Player player;
	private World world;
	boolean reverse;

	public PrivatePlayTask(int frameCount, String animDirPath, World world,
			Player player, boolean reversed) {

		this.player = player;
		this.frames = frameCount;
		this.framesDirPath = animDirPath;
		this.world = world;
		this.reverse = reversed;
		if (reversed) {
			counter = frames - 1;
			frames = 0;
		}
		tm = new TerrainManager(WigglyWorlds.getWep(), world);
	}

	@Override
	public void run() {
		boolean test;
		if (!reverse) {
			test = counter < frames;
		} else {
			test = counter >= frames;
		}

		if (test) {
			File frame = new File(framesDirPath + File.separator + counter);
			try {
				TerrainManager tm = new TerrainManager(WigglyWorlds.getWep(), world);
				CuboidClipboard clipboard = tm.getLocalSessionWithClipboard(frame);
				int x = clipboard.getWidth();
				int y = clipboard.getHeight();
				int z = clipboard.getLength();
				BaseBlock b = null;
				Vector origin = clipboard.getOrigin();
				for (int x1 = 0; x1 < x; x1++) {
					for (int y1 = 0; y1 < y; y1++) {
						for (int z1 = 0; z1 < z; z1++) {
							Vector position = new Vector(x1, y1, z1);
							Location loc = new Location(world,
									origin.getBlockX() + x1, origin.getBlockY()
											+ y1, origin.getBlockZ() + z1);
							try {
								b = clipboard.getBlock(position);
							} catch (ArrayIndexOutOfBoundsException e) {
								Bukkit.getServer()
										.getLogger()
										.info("Index is out of bounds, offending vector is ("
												+ x1
												+ ", "
												+ y1
												+ ", "
												+ z1
												+ ")");
							}

							if (b != null) {
								player.sendBlockChange(loc, b.getId(),
										(byte) b.getData());
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

			if (!reverse) {
				counter++;
			} else {
				counter--;
			}
		} else {
			this.cancel();
			new BukkitRunnable() {
				@Override
				public void run() {
					File frame = new File(framesDirPath + File.separator + "-1");
					TerrainManager tm = new TerrainManager(
							WigglyWorlds.getWep(), world);
					try {
						CuboidClipboard clipboard = tm
								.getLocalSessionWithClipboard(frame);
						int x = clipboard.getWidth();
						int y = clipboard.getHeight();
						int z = clipboard.getLength();
						Vector origin = clipboard.getOrigin();
						for (int x1 = 0; x1 < x; x1++) {
							for (int y1 = 0; y1 < y; y1++) {
								for (int z1 = 0; z1 < z; z1++) {
									Location loc = new Location(world,
											origin.getBlockX() + x1,
											origin.getBlockY() + y1,
											origin.getBlockZ() + z1);
									player.sendBlockChange(loc, loc.getBlock()
											.getType(), loc.getBlock()
											.getData());
								}
							}
						}
					} catch (FilenameException | MaxChangedBlocksException
							| EmptyClipboardException | DataException
							| IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}.runTaskLater(WigglyWorlds.getP(), 100);
		}
	}
}
