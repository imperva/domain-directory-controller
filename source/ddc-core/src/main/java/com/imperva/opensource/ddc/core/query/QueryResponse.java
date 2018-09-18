package com.imperva.opensource.ddc.core.query;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by gabi.beyo on 30/06/2015.
 * Encapsulate a the entire structure of a query response
 */
public class QueryResponse{

    private List<PartitionResponse> partitionResponses = new ArrayList<>();

    /**
     * @return The list of all Partition Responses {@link PartitionResponse}
     */
    public List<PartitionResponse> get(){
        return partitionResponses;
    }

    /**
     * Iterates over the Partition Response {@link PartitionResponse} list and retrieves the actual data {@link EntityResponse} into a single list
     * @return A list of {@link EntityResponse}. A unified list of all the results of all Partition Responses {@link PartitionResponse}
     */
    public List<EntityResponse> getAll() {
        List<EntityResponse> entityResponses = new ArrayList<>();
        for (PartitionResponse partitionResponse : partitionResponses) {
            entityResponses.addAll(partitionResponse.getData());
        }
        return entityResponses;
    }


    /**
     * Iterates over the Partition Responses {@link PartitionResponse} list and calls the hasError() on each PartitionResponse.
     * @return true if any hasError() returns true otherwise false
     */
    public boolean hasError() {
        for(PartitionResponse partitionResponse : this.partitionResponses){
            if(partitionResponse.hasError()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Iterates over the Partition Responses {@link PartitionResponse} list and calls the hasError() on each PartitionResponse.
     * @return true if all hasError() returns true otherwise false
     */
    public boolean isAllError() {
        for(PartitionResponse partitionResponse : this.partitionResponses){
            if(! partitionResponse.isAllError()) {
                return false;
            }
        }
        return true;
    }


    public Status getStatus(String host){
        for(PartitionResponse partitionResponse : this.partitionResponses){
            Status status = partitionResponse.getStatus(host);
            if(status != null) {
                return status;
            }
        }
        return null;
    }

    public void addPartitionResponse(PartitionResponse partitionResponse){
        partitionResponses.add(partitionResponse);
    }

    public void addPartitionResponse(List<PartitionResponse> result) {
        for(PartitionResponse partitionResponse : result){
            this.partitionResponses.add(partitionResponse);
        }
    }
}
