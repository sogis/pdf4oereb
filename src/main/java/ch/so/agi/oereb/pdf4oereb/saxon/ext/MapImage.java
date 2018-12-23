package ch.so.agi.oereb.pdf4oereb.saxon.ext;

import java.awt.image.BufferedImage;

public class MapImage {
	private int layerIndex;
	private double layerOpacity;
	private BufferedImage layerImage;
	
	MapImage(int layerIndex, double layerOpacity, BufferedImage layerImage) {
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

	public double getLayerOpacity() {
		return layerOpacity;
	}

	public void setLayerOpacity(double layerOpacity) {
		this.layerOpacity = layerOpacity;
	}

	public BufferedImage getLayerImage() {
		return layerImage;
	}

	public void setLayerImage(BufferedImage layerImage) {
		this.layerImage = layerImage;
	}
}
