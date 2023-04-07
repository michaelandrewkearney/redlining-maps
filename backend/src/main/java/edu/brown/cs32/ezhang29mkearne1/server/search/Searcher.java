package edu.brown.cs32.ezhang29mkearne1.server.layer.search;

import edu.brown.cs32.ezhang29mkearne1.geoData.GeoJSON;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface Searcher<R extends Searcher.Filterable<E>, E> {
    interface FilterFunction<E> {
        boolean run(E filtered);
    }
    interface Filterable<E> {
        Filterable<E> filter(FilterFunction<E> function);
    }

    R toQuery();
    default R search(FilterFunction<E> function) {
        return (R) toQuery().filter(function);
    }
}
