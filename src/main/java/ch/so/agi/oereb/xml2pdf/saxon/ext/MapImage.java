package ch.so.agi.oereb.xml2pdf.saxon.ext;

import java.awt.image.BufferedImage;

public class MapImage {
	private int layerIndex;
	private int layerOpacity;
	private BufferedImage layerImage;
	
	MapImage(int layerIndex, int layerOpacity, BufferedImage layerImage) {
		this.layerIndex = layerIndex;
		this.layerOpacity = layerOpacity;
		this.layerImage = layerImage;
	}

	public int getLayerIndex() {
		return layerIndex;
	}

	public void setLayerIndex(int layerIndex) {
		this.layerIndex = layerIndex;
	}

	public int getLayerOpacity() {
		return layerOpacity;
	}

	public void setLayerOpacity(int layerOpacity) {
		this.layerOpacity = layerOpacity;
	}

	public BufferedImage getLayerImage() {
		return layerImage;
	}

	public void setLayerImage(BufferedImage layerImage) {
		this.layerImage = layerImage;
	}
}
