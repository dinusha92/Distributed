import java.text.DecimalFormat;

public class Communicator {
	private String ip;
	private int port;
	private String message;

	private DecimalFormat formatter = new DecimalFormat("0000");

	public Communicator(Node node, String message) {
		super();
		this.ip = node.getIp();
		this.port = node.getPort();

		String length_final = formatter.format(message.length() + 5);
		this.message = length_final + " " + message;
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
