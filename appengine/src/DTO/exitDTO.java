package DTO;

import java.io.Serializable;

public class exitDTO implements Serializable {
    int exitStatus;
    public exitDTO(){
        exitStatus = 0;
    }

    public int getExitStatus() {
        return exitStatus;
    }

}
