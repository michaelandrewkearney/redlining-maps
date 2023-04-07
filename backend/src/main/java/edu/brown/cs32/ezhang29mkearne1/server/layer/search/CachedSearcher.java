package edu.brown.cs32.ezhang29mkearne1.server.layer.search;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import edu.brown.cs32.ezhang29mkearne1.server.layer.search.Searcher.Filterable;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

public class CachedSearcher<R extends Filterable<E>, E> implements Searcher<R, E> {
    private static final int DEFAULT_MAXIMUM_SIZE = 1000;
    private static final int DEFAULT_EXPIRE_AFTER_WRITE_IN_MINS = 360;
    private final LoadingCache<FilterFunction<E>, R> cache;
    private final Searcher<R, E> searcher;
    public CachedSearcher(Searcher<R, E> searcher) {
        this(searcher, DEFAULT_MAXIMUM_SIZE, DEFAULT_EXPIRE_AFTER_WRITE_IN_MINS);
    }
    public CachedSearcher(Searcher<R, E> searcher, int maximumSize, int expireAfterWriteInMins) {
        this.searcher = searcher;
        cache = CacheBuilder.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterWrite(Duration.ofMinutes(expireAfterWriteInMins))
                .build(
                        new CacheLoader<>() {
                            public R load(FilterFunction<E> function) throws Exception {
                                return searcher.search(function);
                            }
                        });
    }

    @Override
    public R toQuery() {
        return (R) searcher.toQuery();
    }
    @Override
    public R search(FilterFunction<E> function) {
        try {
            return cache.get(function);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
}
