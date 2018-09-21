package com.imperva.ddc.core.query;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by gabi.beyo on 30/06/2015.
 * Encapsulate a the entire structure of a query response
 */
public class Response {

    private String successfulMessage= "Request return successfully after :";
    private String unSuccessfulMessage= "Request returned with errors";

    public Response(Boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
    }

    public Response() {
    }

    public Boolean getSuccessful() {
        return isSuccessful;
    }

    public void setSuccessful(Boolean successful) {
        isSuccessful = successful;
    }

    private Boolean isSuccessful= true;


    public String getResponseMessage(){
        if(isSuccessful){
            return successfulMessage;
        }
        else
            return unSuccessfulMessage;
    }
}
