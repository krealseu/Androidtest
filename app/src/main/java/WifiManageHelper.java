import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

/**
 * Created by lthee on 2016/9/21.
 */
public class WifiManageHelper {
    static WifiConfiguration CreateConfigure (WifiManager wifiManager){
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        return wifiConfiguration;
    }
}
