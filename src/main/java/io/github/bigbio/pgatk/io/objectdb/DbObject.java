package io.github.bigbio.pgatk.io.objectdb;

import org.zoodb.api.impl.ZooPC;

/**
 * All classes that are stored in the backend need a unique identifier,
 * all further classes inherit from this class.
 *
 * @author Dominik Kopczynski
 */
public class DbObject extends ZooPC {

    //Unique identifier.
    private long id;

    // Flag if object is a first level object or not.
    private boolean firstLevel = true;

    public DbObject(){}

    public long getObjectId() {
        readDBMode();
        return id;
    }

    public void setId(long id){
        writeDBMode();
        this.id = id;
    }

    public boolean getFirstLevel(){
        readDBMode();
        return firstLevel;
    }

    public void setFirstLevel(boolean firstLevel){
        readDBMode();
        this.firstLevel = firstLevel;
    }

    public void readDBMode(){
        try {
            ObjectsDB.increaseRWCounter();
            zooActivateRead();
        }
        finally {
            ObjectsDB.decreaseRWCounter();
        }
    }

    public void writeDBMode(){
        try {
            ObjectsDB.increaseRWCounter();
            zooActivateWrite();
        }
        finally {
            ObjectsDB.decreaseRWCounter();
        }
    }
}
