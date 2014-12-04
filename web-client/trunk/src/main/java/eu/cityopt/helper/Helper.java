package eu.cityopt.helper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Helper {
	public static byte[] getFileBytes(File file) throws IOException {
		ByteArrayOutputStream ous = null;
		InputStream ios = null;
		try {
			byte[] buffer = new byte[4096];
			ous = new ByteArrayOutputStream();
			ios = new FileInputStream(file);
			int read = 0;
			while ((read = ios.read(buffer)) != -1)
				ous.write(buffer, 0, read);
		} finally {
			try {
				if (ous != null)
					ous.close();
			} catch (IOException e) {
				// swallow, since not that important
			}
			try {
				if (ios != null)
					ios.close();
			} catch (IOException e) {
				// swallow, since not that important
			}
		}
		return ous.toByteArray();
	}

}
