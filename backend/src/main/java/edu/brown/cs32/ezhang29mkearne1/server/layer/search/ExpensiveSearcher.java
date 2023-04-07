package edu.brown.cs32.ezhang29mkearne1.server.layer.search;

import edu.brown.cs32.ezhang29mkearne1.server.layer.search.Searcher.Filterable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ExpensiveSearcher<R extends Filterable<E>, E> implements Searcher<R, E> {
    private final R toQuery;
    public ExpensiveSearcher(R toQuery) {
        this.toQuery = toQuery;
    }
    @Override
    public R toQuery() {
        return this.toQuery;
    }
}
