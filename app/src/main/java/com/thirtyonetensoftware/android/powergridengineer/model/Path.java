package com.thirtyonetensoftware.android.powergridengineer.model;

/**
 * Path
 * <p/>
 * Power Grid Engineer
 * 31Ten Software
 * <p/>
 * Author: Josh Kendrick
 */
public class Path {

    private int id;

    private int cost;

    private City source;

    private City destination;

    public Path(int id, int cost, City source, City destination) {
        setId(id);
        setCost(cost);
        setSource(source);
        setDestination(destination);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public City getSource() {
        return source;
    }

    public void setSource(City source) {
        this.source = source;
    }

    public City getDestination() {
        return destination;
    }

    public void setDestination(City destination) {
        this.destination = destination;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }

        Path other = (Path) obj;
        return this.id == other.getId();
    }

    @Override
    public int hashCode() {
        return getId();
    }
}
