package com.xhizors.ChestRefill;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.inventory.ItemStack;

public class ChestRefillReader {

	private ChestRefill instance;

	public ChestRefillReader(ChestRefill instance) {
		this.instance = instance;
	}

	public ConcurrentHashMap<BlockData, ChestData> readChestFile() {
		ConcurrentHashMap<BlockData, ChestData> map = new ConcurrentHashMap<BlockData, ChestData>();
		ArrayList<String> lines = readFileLines("chests");
		int chestNum = 0;

		for (String line : lines) {
			String[] content = line.split("#");
			if (content[0].contains("=")) {
				String worldName;
				int x, y, z;
				long time, next;
				ChestData chestData = new ChestData();
				String[] data = content[0].split("=");
				try {
					String[] values = data[0].split(",");
					worldName = values[0];
					x = Integer.parseInt(values[1]);
					y = Integer.parseInt(values[2]);
					z = Integer.parseInt(values[3]);
					time = Long.parseLong(values[4]);
					next = Long.parseLong(values[5]);
					chestData.setDelay(time);
					chestData.setNextRefill(next);
					
					String[] items = data[1].split(";");
					for (String item : items) {
						if (item.contains(",")) {
							String[] itemValues = item.split(",");
							chestData.addItem(Integer.parseInt(itemValues[0]), Integer.parseInt(itemValues[1]), Integer.parseInt(itemValues[2]), Short.parseShort(itemValues[3]));
						}
					}
				} catch (NumberFormatException e) {
					ChestRefill.log.info("Error reading line: " + line);
					e.printStackTrace();
					continue;
				}
				map.put(findRepeatKey(map, new BlockData(worldName, x, y, z)), chestData);
				chestNum++;
			}
		}
		
		ChestRefill.log.info("[" + ChestRefill.name + "] Loaded " + chestNum + " chests for refill.");
		return map;
	}

	private ArrayList<String> readFileLines(String fileName) {
		ArrayList<String> lines = new ArrayList<String>();
		File file = new File("plugins" + File.separator + "ChestRefill"
				+ File.separator + fileName + ".txt");
		File dir = new File(file.getParent());
		if (!dir.exists()) {
			dir.mkdir();
		}
		if (!file.exists()) {
			try {
				file.createNewFile();
				BufferedWriter out = new BufferedWriter(new FileWriter(file));
				ChestRefill.log.info("[" + ChestRefill.name
						+ "] creating chests file");
				out.write("#Do not edit this file");
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				ChestRefill.log.warning("[" + ChestRefill.name
						+ "] could not create chests file");
			}
		}
		try {
			BufferedReader input = new BufferedReader(new FileReader(file));
			try {
				String line = null;
				while ((line = input.readLine()) != null) {
					lines.add(line);
				}
			} finally {
				input.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return lines;
	}
	
	public void saveFile() {
		File file = new File("plugins" + File.separator + "ChestRefill"
				+ File.separator + "chests.txt");
		File dir = new File(file.getParent());
		if (!dir.exists()) {
			dir.mkdir();
		}
		if (!file.exists()) {
			try {
				file.createNewFile();
				BufferedWriter out = new BufferedWriter(new FileWriter(file));
				ChestRefill.log.info("[" + ChestRefill.name
						+ "] creating chests file");
				out.write("#Do not edit this file");
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				ChestRefill.log.warning("[" + ChestRefill.name
						+ "] could not create chests file");
			}
		}
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			try {
				ConcurrentHashMap<BlockData, ChestData> map = instance.getChestMap();
				writer.write("#Do not edit this file");
				writer.newLine();
				for (BlockData key : map.keySet()) {
					ChestData chest = map.get(key);
					writer.write(key.getWorldName()+","+key.getX()+","+key.getY()+","+key.getZ()+","+chest.getDelay()+","+chest.getNextRefill()+"=");
					for (Integer index : chest.getMap().keySet()) {
						ItemStack itemStack = chest.getMap().get(index);
						writer.write(index+","+itemStack.getTypeId()+","+itemStack.getAmount()+","+itemStack.getDurability()+";");
					}
					writer.newLine();
				}
			} finally {
				writer.flush();
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public BlockData findRepeatKey(ConcurrentHashMap<BlockData, ChestData> map, BlockData b) {
		BlockData key = b;
		for (BlockData k : map.keySet()) {
			if (k.getWorldName().equals(b.getWorldName()) &&
					k.getX() == b.getX() &&
					k.getY() == b.getY() &&
					k.getZ() == b.getZ()) {
				key = k;
				break;
			}
		}
		return key;
	}
}
