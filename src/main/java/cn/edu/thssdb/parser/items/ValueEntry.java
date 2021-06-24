package cn.edu.thssdb.parser.items;

import java.io.Serializable;
import java.util.ArrayList;

public class ValueEntry implements Serializable {
    public ArrayList<String> values;

    public ValueEntry(ArrayList<String> values){
        this.values = values;
    }
}
