package com.owteam.engutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;

/**
 * This has been blatently stolen from
 *
 * @see https://github.com/jenkinsci/git-client-plugin/blob/master/src/main/java/org/jenkinsci/plugins/gitclient/Netrc.java and modfied to make it not related
 * to hudson
 * @see http://opensource.org/licenses/MIT
 * @author bwadleigh
 */
public class Netrc {

	private static final Pattern NETRC_TOKEN = Pattern.compile("(\\S+)");

	private enum ParseState {

		START, REQ_KEY, REQ_VALUE, MACHINE, DEFAULT, LOGIN, PASSWORD, MACDEF, END;
	};

	private File netrc;
	private long lastModified;
	private Map<String, UsernamePasswordCredentials> hosts = new HashMap<String, UsernamePasswordCredentials>();

	/**
	 * Construct netrc using ${user.home}/.netrc or maybe _netrc for windows
	 *
	 * @return
	 */
	public static Netrc getInstance() {
		File netrc = getDefaultFile();
		return getInstance(netrc);
	}

	/**
	 * Construct netrc using given path
	 *
	 * @param netrc path of file to read credentials from
	 * @return
	 */
	public static Netrc getInstance(String netrcPath) {
		File netrc = new File(netrcPath);
		return netrc.exists() ? getInstance(new File(netrcPath)) : null;
	}

	/**
	 * Construct netrc using given File
	 *
	 * @param netrc file to read credentials from
	 * @return
	 */
	public static Netrc getInstance(File netrc) {
		return new Netrc(netrc).parse();
	}

	private static File getDefaultFile() {
		File home = new File(System.getProperty("user.home"));
		File netrc = new File(home, ".netrc");
		if (!netrc.exists()) {
			netrc = new File(home, "_netrc"); // windows variant
		}
		return netrc;
	}

	/**
	 * Get the credentials for the specified host
	 *
	 * @param host The name of the machine the credentials are for exactly, this should match the token following machine in the netrc
	 * @return The credentials containing the login and password.
	 * @throws Exception
	 */
	public Credentials getCredentials(String host) {
		if (!this.netrc.exists()) {
			return null;
		}
		if (this.lastModified != this.netrc.lastModified()) {
			parse();
		}
		return this.hosts.get(host);
	}

	private Netrc(File netrc) {
		this.netrc = netrc;
	}

	synchronized private Netrc parse() {
		if (!netrc.exists()) {
			return this;
		}

		this.hosts.clear();
		this.lastModified = this.netrc.lastModified();

		BufferedReader r = null;
		try {

			//Using a RandomAccessFile instead of a BufferedReader to track position in case of error
			r = new BufferedReader(new FileReader(netrc));
			String line = null;
			String machine = null;
			String login = null;
			String password = null;

			ParseState state = ParseState.START;
			Matcher matcher = NETRC_TOKEN.matcher("");
			while ((line = r.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty()) {
					if (state == ParseState.MACDEF) {
						state = ParseState.REQ_KEY;
					}
					continue;
				}

				matcher.reset(line);
				while (matcher.find()) {
					String match = matcher.group();
					switch (state) {
						case START:
							if ("machine".equals(match)) {
								state = ParseState.MACHINE;
							}
							break;

						case REQ_KEY:
							if ("login".equals(match)) {
								state = ParseState.LOGIN;
							} else if ("password".equals(match)) {
								state = ParseState.PASSWORD;
							} else if ("macdef".equals(match)) {
								state = ParseState.MACDEF;
							} else if ("machine".equals(match)) {
								state = ParseState.MACHINE;
							} else if ("default".equals(match)) {
								if (machine != null && login != null && password != null) {
									this.hosts.put(machine, new UsernamePasswordCredentials(login, password));
								}
								machine = "";
								login = null;
								password = null;
								state = ParseState.REQ_KEY;
							} else {
								state = ParseState.REQ_VALUE;
							}
							break;

						case REQ_VALUE:
							state = ParseState.REQ_KEY;
							break;

						case MACHINE:
							if (machine != null && login != null && password != null) {
								this.hosts.put(machine, new UsernamePasswordCredentials(login, password));
							}
							machine = match;
							login = null;
							password = null;
							state = ParseState.REQ_KEY;
							break;

						case LOGIN:
							login = match;
							state = ParseState.REQ_KEY;
							break;

						case PASSWORD:
							password = match;
							state = ParseState.REQ_KEY;
							break;

						case MACDEF:
							// Only way out is an empty line, handled before the find() loop.
							break;

					}
				}
			}
			if (machine != null && login != null && password != null) {
				this.hosts.put(machine, new UsernamePasswordCredentials(login, password));
			}

		} catch (IOException ex) {

			Logger.getLogger(Netrc.class.getName()).log(Level.SEVERE, null, ex);
			//throw new ParseException("Invalid netrc file: '" + this.netrc.getAbsolutePath() + "'", (int) r.getFilePointer());
		} finally {
			try {
				r.close();
			} catch (IOException ex) {
				Logger.getLogger(Netrc.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		return this;
	}

}
