package com.dzarembo.okxservice.cache;

import com.dzarembo.okxservice.model.FundingRate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FundingCache {
    private final Map<String, FundingRate> cache = new ConcurrentHashMap<>();

    public void putAll(Collection<FundingRate> rates) {
        cache.clear();
        rates.forEach(rate -> cache.put(rate.getSymbol(), rate));
    }

    public Collection<FundingRate> getAll() {
        return cache.values();
    }
}
