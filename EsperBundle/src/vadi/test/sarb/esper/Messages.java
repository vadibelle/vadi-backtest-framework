package vadi.test.sarb.esper;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

public class Messages {
	private static final String BUNDLE_NAME = "vadi.test.sarb.esper.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);
	private static Properties props;
	
	
	private Messages() {
		
	}
	
	public static void loadProperties(String filename)
	{
		try {
			props = new Properties();
			InputStream in = new FileInputStream(filename);
			props.load(in);
			in.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Cannot load system properties");
		}
	}
	public static String getString(String key) {
		try {
			if (props.containsKey(key))
				return props.getProperty(key);
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
	
	public static void setProperty(String key, String value){
		props.setProperty(key, value);
	}
}
