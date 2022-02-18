package ch.so.agi.oereb.pdf4oereb.utils;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import javax.imageio.ImageIO;

public class Image {
	public static byte[] getImage(String request) throws Exception {
		String decodedRequest;
		try {
			decodedRequest = java.net.URLDecoder.decode(request, "UTF-8");
			
			HttpClient httpClient = HttpClient.newBuilder()
			        .followRedirects(Redirect.ALWAYS)
			        .build();

			HttpRequest httpRequest = HttpRequest.newBuilder()
			        .uri(new URI(decodedRequest))
			        .GET()
			        .build();
			
			HttpResponse<InputStream> response = httpClient
			        .send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());

			InputStream inputStream = response.body();
			BufferedImage image = ImageIO.read(inputStream);

			// FOP is picky when it comes to 8bit png images.
			// Convert them to 24bit + Alpha.
			// Der Alpha-Kanal wird erst beim Zusammenfügen mit anderen Bildern zwecks
			// PDF-Konformität entfernt.
            BufferedImage fixedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR_PRE);
            
            Graphics2D g = (Graphics2D) fixedImage.getGraphics();
            g.drawImage(image, 0, 0, null);
                                   
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(fixedImage, "png", baos); 
            baos.flush();
            byte[] fixedImageInByte = baos.toByteArray();
            baos.close();          
            
			return fixedImageInByte;		
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
}
