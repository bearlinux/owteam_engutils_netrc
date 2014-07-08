package com.owteam.engUtils;

import java.io.IOException;
import java.net.*;
import java.security.KeyManagementException;
import javax.net.ssl.*;
import java.security.cert.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author bwadleigh
 */
public class CertTool {

	// Create a trust manager that does not validate certificate chains, 
	public static Certificate[] getCerts(URL url) throws NoSuchAlgorithmException, KeyManagementException, MalformedURLException, IOException, CertificateEncodingException {
		HttpsURLConnection conn;
		TrustManager[] trustAllCerts = new TrustManager[]{
			new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}

				public void checkClientTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
				}
			}
		};

		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		SSLSocketFactory goodSSLSF = HttpsURLConnection.getDefaultSSLSocketFactory();
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		conn = (HttpsURLConnection) url.openConnection();
		conn.connect();
		HttpsURLConnection.setDefaultSSLSocketFactory(goodSSLSF);
		for (Certificate cert : conn.getServerCertificates()) {
			if (cert instanceof X509Certificate) {
				StringBuffer result = new StringBuffer();
				int i = 0;
				//println(it);
				MessageDigest sha1 = MessageDigest.getInstance("SHA1");
				sha1.update(cert.getEncoded());
				for (byte b : sha1.digest()) {
					if (i != 0) {
						result.append(":");
					}
					result.append(String.format("%02X", b));
					i++;
				}

			}
		}

		return conn.getServerCertificates();
	}

	public static String fingerprint(Certificate cert) throws NoSuchAlgorithmException, CertificateEncodingException {
		StringBuffer result = new StringBuffer();
		int i = 0;
		//println(it);
		MessageDigest sha1 = MessageDigest.getInstance("SHA1");
		sha1.update(cert.getEncoded());
		for (byte b : sha1.digest()) {
			if (i != 0) {
				result.append(":");
			}
			result.append(String.format("%02X", b));
			i++;
		}
		return result.toString();
	}
}
