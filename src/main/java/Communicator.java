public class Communicator {
	private String ip;
	private int port;
	private String message;

	public Communicator(String ip, int port, String message) {
		super();
		this.ip = ip;
		this.port = port;
		this.message = message;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return ip + ":" + port + " - " + message;
	}
}
