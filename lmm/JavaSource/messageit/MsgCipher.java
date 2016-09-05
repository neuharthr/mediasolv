package messageit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;


public class MsgCipher {

	
	private static MsgCipher inst = null;
	private InputStream cis = null;
	private OutputStream cos = null;

	private Cipher desCipherIn = null;
	private Cipher desCipherOut = null;

	private static boolean IS_ENCRYPTION = false;
	private static final byte[] KEY = "@[som++-_crz_monkey_in it ^ dig it@!".getBytes();


	public MsgCipher() {
		super();
		init();
	}
	
	private void init() {
		
		try {
			DESKeySpec dskeySpec = new DESKeySpec( KEY );
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey secretKey = keyFactory.generateSecret( dskeySpec );
			
			desCipherIn = Cipher.getInstance("DES/ECB/PKCS5Padding");
			desCipherIn.init( Cipher.DECRYPT_MODE, secretKey );
			
			desCipherOut = Cipher.getInstance("DES/ECB/PKCS5Padding");
			desCipherOut.init( Cipher.ENCRYPT_MODE, secretKey );			
		} catch( Exception ex )  {
			
		}
	}
	
	public static synchronized MsgCipher getInstance() {
		if( inst == null ) 
			inst = new MsgCipher();
		return inst;
	}
	
	public synchronized InputStream getCipherInputStream( InputStream is ) {
		if( cis == null ) {
			if( IS_ENCRYPTION ) 
				cis = new CipherInputStream( is, desCipherIn );
			else //no encryption, just use what was passed in
				return is;
		}
		
		return cis;		
	}

	public synchronized OutputStream getCipherOutputStream( OutputStream os ) {
		if( cos == null ) {
			if( IS_ENCRYPTION ) 
				cos = new CipherOutputStream( os, desCipherOut );
			else //no encryption, just use what was passed in
				return os;
		}
		
		return cos;		
	}
	
	public static void main( String[] args ){
		
		try {
			String t = "hello all you crazy folks!!!!";

			System.out.println( "Start: " + t.toString().length() );

			ByteArrayOutputStream bos = new ByteArrayOutputStream();			
			ObjectOutputStream oos = new ObjectOutputStream(
					MsgCipher.getInstance().getCipherOutputStream(bos) );

			oos.writeObject( t );
			oos.flush();
			oos.close();

			System.out.println( "encrypted: " + bos.toString().length() );
			
			ByteArrayInputStream bis = new ByteArrayInputStream( bos.toByteArray() );
			ObjectInputStream ois = new ObjectInputStream(
					MsgCipher.getInstance().getCipherInputStream(bis) );
					
			String tB = (String)ois.readObject();
			ois.close();

			System.out.println( "decrypted: " + tB.toString().length() );
		}
		catch( Exception ex ) {
			ex.printStackTrace( System.out );
		}

	}
}
