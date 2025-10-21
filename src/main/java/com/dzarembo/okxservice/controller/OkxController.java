package com.dzarembo.okxservice.controller;

import com.dzarembo.okxservice.cache.FundingCache;
import com.dzarembo.okxservice.model.FundingRate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api/okx")
@RequiredArgsConstructor
public class OkxController {

    private final FundingCache cache;

    @GetMapping("/funding")
    public Collection<FundingRate> getFundingRates() {
        return cache.getAll();
    }
}