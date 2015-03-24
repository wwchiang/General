import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class LevelBuilder {

	public LevelBuilder() {
	}

	/**
	 * @param args
	 */
	@SuppressWarnings("resource")
	public Path convertFile(File file) {

		Scanner scan = null;

		try {
			scan = new Scanner(file);
		} catch (FileNotFoundException e1) {
			System.out.println("Error: File not found.");
		}

		/* Data structures used to make things work */
		ArrayList<Tile> data = new ArrayList<>();
		String line = null;

		String cube = scan.nextLine();
		String bounce = scan.nextLine();
		String trap = scan.nextLine();
		String br = scan.nextLine();
		String unstable = scan.nextLine();
		String saw = scan.nextLine();
		String lock = scan.nextLine();
		String key = scan.nextLine();
		String cake = scan.nextLine();

		/* Ensures to fit the window size of 960 x 640 [CAPS] */
		int height = 9;
		// int width = 14;

		int j = height;

		/* Do not tweak this */
		while (scan.hasNextLine()) {
			line = scan.nextLine();
			for (int k = 0; k < line.length(); k++) {
				String temp = "";
				temp += line.charAt(k);
				Tile tile = new Tile(temp, cube, bounce, trap, br, unstable,
						cake, saw, lock, key);
				if (tile.isTile) {
					tile.setCoordinates(k, j);
				}
				data.add(tile);
			}
			j--;
		}

		/* Prints everything out */
		try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(
				Paths.get("LevelOutput.XML"), Charset.forName("UTF-8")))) {

			/* Header */
			writer.println("<?xml version=" + "\"1.0\"" + " encoding="
					+ "\"utf-8\"" + " ?>");
			writer.println("<XnaContent>");
			writer.println("  <Asset Type=" + "\"Project2.MapTileData[]\""
					+ ">");

			/* Iterates through the actual data and outputs it onto the xml file */
			for (Tile t : data) {

				if (t.isTile) {
					writer.println("    <Item>");

					/*
					 * Feel free to change the names of tileTexture/etc but make
					 * sure you stay consistent in closing the tags
					 */
					writer.println("      <tileTexture>" + t.tileTexture
							+ "</tileTexture>");
					writer.println("      <isTrap>" + t.isTrap + "</isTrap>");
					writer.println("      <isBouncy>" + t.isBouncy
							+ "</isBouncy>");
					writer.println("      <isBreakable>" + t.isBreakable
							+ "</isBreakable>");
					writer.println("      <isUnstable>" + t.isUnstable
							+ "</isUnstable>");
					writer.println("      <isSaw>" + t.isSaw + "</isSaw>");
					writer.println("      <isLock>" + t.isLock + "</isLock>");
					writer.println("      <isKey>" + t.isKey + "</isKey>");
					writer.println("      <isCake>" + t.isCake + "</isCake>");
					writer.println("      <mapPosition>" + t.xpos + " "
							+ t.ypos + "</mapPosition>");

					/* End tag to categorize each item */
					writer.println("    </Item>");
				}
			}

			/* End header */
			writer.println("  </Asset>");
			writer.println("</XnaContent>");
		} catch (IOException e) {
			System.out.println("Unable to write file.");
			// e.printStackTrace();
		}

		return Paths.get("LevelOutput.XML");
	}
}
