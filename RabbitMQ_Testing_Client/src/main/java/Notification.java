import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class Notification {

    private String topic;
    private String message;
    private String provider;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date timeStamp;
    private int priority;

    public String getTopic(){
        return topic;
    }

    public String getMessage(){
        return message;
    }

    public String getProvider(){
        return provider;
    }

    public Date getTimeStamp(){
        return timeStamp;
    }

    public int getPriority(){
        return priority;
    }

    @Override
    public String toString(){
        return "" +
                topic + "\n" +
                timeStamp + "\n\n" +
                message + "\n" +
                provider + "\n";

    }
}
