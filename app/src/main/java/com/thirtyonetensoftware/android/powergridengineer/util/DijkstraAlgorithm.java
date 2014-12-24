package com.thirtyonetensoftware.android.powergridengineer.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.os.Parcel;
import android.os.Parcelable;

import com.thirtyonetensoftware.android.powergridengineer.model.City;
import com.thirtyonetensoftware.android.powergridengineer.model.Path;

/**
 * DijkstraAlgorithm
 * <p/>
 * Power Grid Engineer
 * 31Ten Software
 * <p/>
 * Author: Josh Kendrick
 */
public enum DijkstraAlgorithm {

    INSTANCE;

    /**
     * Method that makes use of Dijkstra's Algorithm to find the shortest path between 2 cities on
     * the game board
     *
     * @param source      city you are building from
     * @param destination city you are building to
     * @return route HashMap<City, Path> To get the route, you will need to loop through the
     * HashMap; starting with the destination node, get the path value of the destination node key,
     * then get the path value of the previous path value's destination key, and so on until you
     * reach the source node.
     * <p/>
     * This code was written based on the Algorithm portion of the Dijkstra's Algorithm Wikipedia
     * article (http://en.wikipedia.org/wiki/Dijkstras_algorithm).
     */
    public static HashMap<City, Path> determineShortestRoute(City source, City destination) {
        HashMap<City, Path> route = new HashMap<>();

        /**
         * If the source is the same as the destination the shortest path is no path
         */
        if ( source.equals(destination) ) {
            return route;
        }

        HashMap<City, Set<Path>> graph = Graph.getGraph();
        HashMap<City, Integer> distanceFromSource = new HashMap<>();

        // Set currentCity as source because we're evaluating the source node first
        City currentCity = source;
        int tentativeVal, distance, smallest;
        Integer currentVal;

        /**
         * #1 of the algorithm: give every node (city) a tentative distance; 0 for the source
         * node and infinity for all others.
         *
         * The way this is accomplished here is all nodes are set to 'infinity' by not existing
         * in the distances map, and source is put in the distances map with a value of 0
         *
         * #2 is: Mark all nodes as unsolved. Set the source node as the current node. Create a
         * set of the unsolved nodes.
         *
         * All nodes are marked as unsolved by being keys in the graph. The source node is set as
         * the current node above.
         */
        distanceFromSource.put(currentCity, 0);
        route.put(source, null);

        // graph.size will be the number of unsolved cities
        while ( graph.size() > 0 ) {
            /**
             * #3 is: for the current node, calculate the tentative distance to all it's
             * neighbors. The tentative distance is calculated by taking the current node's
             * distance and adding the distance from current to the neighbor. Compare this
             * calculated tentative value to any distance value the neighbor already has and
             * assign the smaller of the two to the neighbor.
             *
             * Ex: Current node A is marked with a distance 6 (it is 6 from the source). It is 2
             * away from it's neighbor B, so the tentative distance is 6 + 2 = 8. B already had a
             * distance value assigned to it of 10 from a previous iteration,
             * so give it the value 8 now.
             *
             * This is accomplished here by getting the outgoing paths for a city,
             * then iterate through those paths.
             * If the currentVal doesnt exist (is infinity) or is greater than tentativeVal,
             * then update the distance map and add that path to the route. Otherwise do nothing.
             */
            distance = distanceFromSource.get(currentCity);
            if ( graph.containsKey(currentCity) ) {
                for ( Path path : graph.get(currentCity) ) {
                    tentativeVal = distance + path.getCost();

                    currentVal = distanceFromSource.get(path.getDestination());
                    if ( currentVal == null || tentativeVal < currentVal ) {
                        distanceFromSource.put(path.getDestination(), tentativeVal);
                        route.put(path.getDestination(), path);
                    }
                }

                /**
                 * #4 is: after evaluating all the neighbors, mark the current node as solved and
                 * remove it from the unsolved set.
                 *
                 * We do this by removing it from the graph.
                 */
                graph.remove(currentCity);
            }
            else {
                boolean found = false;
                for ( City city : graph.keySet() ) {
                    if ( distanceFromSource.containsKey(city) ) {
                        found = true;
                    }
                }

                if ( !found ) {
                    return new HashMap<>();
                }
            }

            /**
             * #5 is ending conditions. If currentCity is the destination,
             * you can quit. If the smallest tentativeVal among the unsolved nodes is infinity
             * (this occurs when there is no connection between the current node and the
             * remaining unsolved nodes. In our case that would be none of the cities in the
             * graph are in the distanceFromSource map, this is checked in the for loop just above)
             *
             * We do this by breaking out of the loop if currentCity is the destination
             */
            if ( currentCity.equals(destination) ) {
                break;
            }

            /**
             * #6 is select the unsolved node that has the smallest distance from a solved node,
             * set it as the new current node, and go back to step 3.
             *
             * We do this here by setting smallest to the largest Integer value. Then we loop
             * through the unsolved cities (the graph's keys). We get the distanceFromSource for
             * a city, if it's not null and it's value is less than our current smallest,
             * that city becomes the new currentCity for the next iteration through the algorithm
             */
            smallest = Integer.MAX_VALUE;
            for ( City city : graph.keySet() ) {
                currentVal = distanceFromSource.get(city);
                if ( currentVal != null && currentVal < smallest ) {
                    smallest = currentVal;
                    currentCity = city;
                }
            }
        }

        return route;
    }

    public static Result processRoute(HashMap<City, Path> route, City destination, int stepCost) {
        Result result = new Result();

        if ( route.size() < 1 ) {
            return result;
        }

        Path path = route.get(destination);
        result.insertCity(destination, 0);
        while ( path != null ) {
            result.insertCity(path.getSource(), 0);
            result.incrementCost(path.getCost());
            path = route.get(path.getSource());
        }
        result.setStepCost(stepCost);

        return result;
    }

    public static class Result implements Parcelable {

        public static final String NO_CONNECTION_TEXT = "NO CONNECTION";

        public static final Parcelable.Creator<Result> CREATOR = new Parcelable.Creator<Result>() {
            public Result createFromParcel(Parcel in) {
                return new Result(in);
            }

            public Result[] newArray(int size) {
                return new Result[size];
            }
        };

        private StringBuilder builder;

        private ArrayList<City> cities = new ArrayList<>();

        private int cost;

        private int stepCost;

        private Result() {
        }

        private Result(Parcel in) {
            in.readTypedList(cities, City.CREATOR);
            setCost(in.readInt());
            setStepCost(in.readInt());
        }

        public void insertCity(City city, int index) {
            cities.add(index, city);
        }

        public void incrementCost(int cost) {
            this.cost += cost;
        }

        public int getCost() {
            return cost;
        }

        public void setCost(int cost) {
            this.cost = cost;
        }

        public int getStepCost() {
            return stepCost;
        }

        public void setStepCost(int stepCost) {
            this.stepCost = stepCost;
        }

        public String getPath() {
            if ( cities.size() < 1 ) {
                return NO_CONNECTION_TEXT;
            }

            boolean first = true;

            builder = new StringBuilder();
            for ( City city : cities ) {
                if ( first ) {
                    builder.append(city.getName());
                    first = false;
                }
                else {
                    builder.append(" -> ").append(city.getName());
                }
            }

            return builder.toString().toLowerCase();
        }

        @Override
        public String toString() {
            return getPath() + " -> " + (getCost() + getStepCost());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedList(cities);
            dest.writeInt(getCost());
            dest.writeInt(getStepCost());
        }
    }
}
