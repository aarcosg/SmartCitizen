package us.idinfor.smartcitizen.event;

import com.google.android.gms.common.ConnectionResult;

/**
 * Created by Alvaro on 25/02/2016.
 */
public class ConnectionResultEvent {

    private ConnectionResult result;

    public ConnectionResultEvent(ConnectionResult result) {
        this.result = result;
    }

    public ConnectionResult getResult() {
        return result;
    }

    public void setResult(ConnectionResult result) {
        this.result = result;
    }
}
