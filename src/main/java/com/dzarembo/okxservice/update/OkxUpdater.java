package com.dzarembo.okxservice.update;


import com.dzarembo.okxservice.cache.FundingCache;
import com.dzarembo.okxservice.client.OkxApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OkxUpdater {

    private final FundingCache cache;
    private final OkxApiClient apiClient;

    @Scheduled(fixedRate = 5 * 60 * 1000) // обновление каждые 5 минут
    public void updateFundingRates() {
        log.info("Updating OKX funding cache...");
        cache.putAll(apiClient.fetchFundingRates());
        log.info("OKX funding cache updated: {} entries", cache.getAll().size());
    }
}
