package org.kreal.ftpserver.virtualfilesystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lthee on 2016/10/17.
 */
public class DoubleList<K,V> {
    private List<K> keyList = new ArrayList<>();
    private List<V> valueList = new ArrayList<>();
    V put(K key , V value){
        int pos = this.keyList.indexOf(key);
        V oldvalue = null;
        if (pos == -1){
            this.keyList.add(key);
            this.valueList.add(value);
        }
        else {
            oldvalue = this.valueList.get(pos);
            this.keyList.add(pos,key);
            this.valueList.add(pos,value);
        }
        return oldvalue;
    }

    V get(K key){
        int pos = this.keyList.indexOf(key);
        if (pos == -1){
            return null;
        }
        else return valueList.get(pos);
    }

    List<K> getKeyList(){
        return this.keyList;
    }

}
