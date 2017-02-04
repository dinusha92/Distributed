/**
 * Created by kasun on 2/4/17.
 */
public class Stat {
    private int receivedMessages;
    private int sentMessages;
    private int answeredMessages;
    private int latencyMin;
    private int latencyMax;
    private double latencySD;
    private double latencyAverage;
    private int hopsMin;
    private int hopsMax;
    private double hopsSD;
    private double hopsAverage;
    private String sep = "#";

    public Stat(){

    }

    public Stat(String encodedStat){
        String [] str = encodedStat.split(sep);
        receivedMessages = Integer.parseInt(str[0]);
        sentMessages = Integer.parseInt(str[1]);
        answeredMessages = Integer.parseInt(str[2]);
        latencyMin = Integer.parseInt(str[3]);
        latencyMax = Integer.parseInt(str[4]);
        latencySD = Double.parseDouble(str[5]);
        latencyAverage = Double.parseDouble(str[6]);
        hopsMin = Integer.parseInt(str[7]);
        hopsMax = Integer.parseInt(str[8]);
        hopsSD = Double.parseDouble(str[9]);
        hopsAverage = Double.parseDouble(str[10]);
    }

    public String getEncodedStat(){
        return receivedMessages+sep+sentMessages+sep+answeredMessages+sep
                +latencyMin+sep+latencyMax+sep+latencySD+sep+latencyAverage+sep
                +hopsMin+sep+hopsMax+sep+hopsSD+hopsAverage+sep;
    }

    public int getReceivedMessages() {
        return receivedMessages;
    }

    public void setReceivedMessages(int receivedMessages) {
        this.receivedMessages = receivedMessages;
    }

    public int getSentMessages() {
        return sentMessages;
    }

    public void setSentMessages(int sentMessages) {
        this.sentMessages = sentMessages;
    }

    public int getAnsweredMessages() {
        return answeredMessages;
    }

    public void setAnsweredMessages(int answeredMessages) {
        this.answeredMessages = answeredMessages;
    }

    public int getLatencyMin() {
        return latencyMin;
    }

    public void setLatencyMin(int latencyMin) {
        this.latencyMin = latencyMin;
    }

    public int getLatencyMax() {
        return latencyMax;
    }

    public void setLatencyMax(int latencyMax) {
        this.latencyMax = latencyMax;
    }

    public double getLatencySD() {
        return latencySD;
    }

    public void setLatencySD(double latencySD) {
        this.latencySD = latencySD;
    }

    public int getHopsMin() {
        return hopsMin;
    }

    public void setHopsMin(int hopsMin) {
        this.hopsMin = hopsMin;
    }

    public int getHopsMax() {
        return hopsMax;
    }

    public void setHopsMax(int hopsMax) {
        this.hopsMax = hopsMax;
    }

    public double getHopsSD() {
        return hopsSD;
    }

    public void setHopsSD(double hopsSD) {
        this.hopsSD = hopsSD;
    }

    public double getLatencyAverage() {
        return latencyAverage;
    }

    public void setLatencyAverage(double latencyAverage) {
        this.latencyAverage = latencyAverage;
    }

    public double getHopsAverage() {
        return hopsAverage;
    }

    public void setHopsAverage(double hopsAverage) {
        this.hopsAverage = hopsAverage;
    }
}
