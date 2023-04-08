package edu.brown.cs32.ezhang29mkearne1.server.layer.search;

import edu.brown.cs32.ezhang29mkearne1.server.layer.search.Searcher.Filterable;

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
