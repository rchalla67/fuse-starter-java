package org.galatea.starter.service;

import java.util.List;
import org.galatea.starter.domain.IexHistoricalPrices;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "IEXCLOUD", url = "${spring.rest.iexCloudApiBasePath}")
public interface IexCloudApiClient {

  @GetMapping("/stock/{symbol}/chart/{range}/{date}?token=${APITOKEN}")
  List<IexHistoricalPrices> getHistoricalPrices(@PathVariable("symbol") String symbol,
      @PathVariable("range") String range,
      @PathVariable(required = false) String date);
}
