package net.lintford.library.data.cellworld;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.lintford.library.core.graphics.textures.Texture;

public abstract class CellWorldLoader {

	public boolean loadLevel(int pLevelNum, CellGridLevel pLevel, String pResName) {

		BufferedImage lImage = null;
		int[] lLevelData = null;

		int lWidth;
		int lHeight;

		// 1. load the image
		try {
			lImage = ImageIO.read(Texture.class.getResourceAsStream(pResName));
			lWidth = lImage.getWidth();
			lHeight = lImage.getHeight();

			int[] lPixels = new int[lWidth * lHeight];
			lImage.getRGB(0, 0, lWidth, lHeight, lPixels, 0, lWidth);

			// 2. change channel order
			lLevelData = new int[lWidth * lHeight];
			for (int i = 0; i < lWidth * lHeight; i++) {

				// Work out the image x,y
				int lPosX = (i % lWidth);
				int lPosY = (i / lHeight);

				int col = lPixels[i];

				onColorLoaded(col, lPosX, lPosY, lLevelData);

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		// pLevel.setLevelTiles(lLevelData);

		return true;

	}

	protected abstract void onColorLoaded(int pCol, int x, int y, int[] levelData);

}
