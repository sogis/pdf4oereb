package ch.so.agi.oereb.pdf4oereb.saxon.ext;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;

public class WebMapService {
	public static byte[] getMap(String request) throws Exception {
		String decodedRequest;
		try {
			decodedRequest = java.net.URLDecoder.decode(request, "UTF-8");
			System.out.println("decodedRequest: " + decodedRequest);
			
			CloseableHttpClient httpclient = HttpClients.custom()
					.setRedirectStrategy(new LaxRedirectStrategy()) // adds HTTP REDIRECT support to GET and POST methods 
					.build();
			
			HttpGet get = new HttpGet(new URL(decodedRequest).toURI()); 
			CloseableHttpResponse response = httpclient.execute(get);
//            System.out.println("response: " + response.toString());
//            System.out.println("response: " + response.getEntity().getContentLength());
//            System.out.println("response: " + response.getEntity());
//            System.out.println("response: " + response.getStatusLine().getStatusCode());
//			
//            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
//            String line = null;
//            while((line = in.readLine()) != null) {
//              System.out.println(line);
//            }

            
            
			InputStream inputStream = response.getEntity().getContent();
			BufferedImage image = ImageIO.read(inputStream);
			
			System.out.println("image: " + image);

			// FOP is picky when it comes to 8bit png images.
			// Convert them to 24bit.
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
