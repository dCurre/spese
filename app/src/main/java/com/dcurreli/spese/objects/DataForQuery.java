package com.dcurreli.spese.objects;

public class DataForQuery {

    private Double startsAt;
    private Double endsAt;

    public DataForQuery(Double startsAt, Double endsAt){
        this.startsAt = startsAt-1;
        this.endsAt = endsAt+1;
    }

    public Double getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(Double startsAt) {
        this.startsAt = startsAt;
    }

    public Double getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(Double endsAt) {
        this.endsAt = endsAt;
    }
}
