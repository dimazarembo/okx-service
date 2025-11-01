package com.dzarembo.okxservice.client;

import com.dzarembo.okxservice.model.FundingRate;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class OkxApiClient {

    private final WebClient webClient = WebClient.create("https://www.okx.com");

    public Collection<FundingRate> fetchFundingRates() {
        try {
            OkxResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v5/public/funding-rate")
                            .queryParam("instId", "ANY")
                            .build())
                    .retrieve()
                    .bodyToMono(OkxResponse.class)
                    .block();

            if (response == null || response.getData() == null) {
                log.warn("Empty response from OKX");
                return List.of();
            }

            return response.getData().stream()
                    // 1️⃣ фильтруем только USDT контракты
                    .filter(item -> item.getInstId() != null && item.getInstId().contains("USDT"))
                    // 2️⃣ исключаем USD контракты
                    .filter(item -> !item.getInstId().contains("USD-SWAP") && !item.getInstId().contains("USD-"))
                    // 3️⃣ мапим в FundingRate
                    .map(this::mapToFundingRate)
                    .filter(Objects::nonNull)
                    .toList();

        } catch (Exception e) {
            log.error("Failed to fetch funding rates from OKX", e);
            return List.of();
        }
    }

    private FundingRate mapToFundingRate(OkxResponse.Item item) {
        try {
            double rate = Double.parseDouble(item.getFundingRate());
            long fundingTimeUtc = Long.parseLong(item.getFundingTime());
            long nextFundingTimeUtc = Long.parseLong(item.getNextFundingTime());
            int intervalHours = computeIntervalHours(fundingTimeUtc, nextFundingTimeUtc);

            String normalizedSymbol = normalizeSymbol(item.getInstId());

            log.debug("OKX: {} rate={} fundingTime(UTC)={} nextFundingTime(UTC)={} interval={}h",
                    normalizedSymbol, rate,
                    Instant.ofEpochMilli(fundingTimeUtc),
                    Instant.ofEpochMilli(nextFundingTimeUtc),
                    intervalHours);

            return new FundingRate(
                    normalizedSymbol,
                    rate,
                    fundingTimeUtc,
                    intervalHours
            );

        } catch (Exception e) {
            log.error("Failed to parse OKX item: {}", item, e);
            return null;
        }
    }

    private int computeIntervalHours(long fundingTime, long nextFundingTime) {
        long diff = nextFundingTime - fundingTime;
        return (int) (diff / (1000 * 60 * 60));
    }

    private String normalizeSymbol(String instId) {
        // Пример: "BTC-USDT-SWAP" → "BTCUSDT"
        return instId.replace("-", "").replace("SWAP", "");
    }

    @Data
    public static class OkxResponse {
        private List<Item> data;

        @Data
        public static class Item {
            private String instId;
            private String fundingRate;
            private String fundingTime;
            private String nextFundingTime;
        }
    }
}
