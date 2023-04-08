package edu.brown.cs32.ezhang29mkearne1.server.layer.search;

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
