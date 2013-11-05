package io.polaroid;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.imageio.ImageIO;

public class Main {

	public static void main(String[] args) throws NoSuchAlgorithmException, Exception {
		final File input = new File("D:/YTEST.jpg");
		final BufferedImage buffImg = ImageIO.read(input);
		try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			ImageIO.write(buffImg, "jpg", outputStream);
			final byte[] data = outputStream.toByteArray();
			//			System.out.println("Start MD5 Digest");
			final byte[] hash = hash(data);

			System.out.println(returnHex(hash));
		}
	} // Belongs to main class

	private static byte[] hash(final byte[] data) throws NoSuchAlgorithmException {
		final MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(data);
		final byte[] hash = md.digest();
		return hash;
	}

	// Below method of converting Byte Array to hex
	// Can be found at: http://www.rgagnon.com/javadetails/java-0596.html
	static String returnHex(byte[] inBytes) throws Exception {
		String hexString = "";
		for (int i = 0; i < inBytes.length; i++) { //for loop ID:1
			hexString += Integer.toString((inBytes[i] & 0xff) + 0x100, 16).substring(1);
		} // Belongs to for loop ID:1
		return hexString;
	} // Belongs to returnHex class
		//b80e980c1d5597a92dc740194a6bd3ad
	//dc43fb47ba348c7e966136a65875fda6
}
