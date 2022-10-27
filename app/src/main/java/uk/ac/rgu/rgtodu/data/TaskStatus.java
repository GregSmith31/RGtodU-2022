package uk.ac.rgu.rgtodu.data;

public enum TaskStatus {
    NOT_STARTED("NOT_STARTED"),
    IN_PROGRESS("IN_PROGRESS"),
    COMPLETE("COMPLETE");

    private String label;

    private TaskStatus(String label){
        this.label = label;
    }

    public String getLabel(){
        return this.label;
    }
}
