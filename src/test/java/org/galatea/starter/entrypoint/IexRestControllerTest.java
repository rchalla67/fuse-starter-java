package org.galatea.starter.entrypoint;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Collections;
import junitparams.JUnitParamsRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.ASpringTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


@RequiredArgsConstructor
@Slf4j
// We need to do a full application start up for this one, since we want the feign clients to be instantiated.
// It's possible we could do a narrower slice of beans, but it wouldn't save that much test run time.
@SpringBootTest
// this gives us the MockMvc variable
@AutoConfigureMockMvc
// we previously used WireMockClassRule for consistency with ASpringTest, but when moving to a dynamic port
// to prevent test failures in concurrent builds, the wiremock server was created too late and feign was
// already expecting it to be running somewhere else, resulting in a connection refused
@AutoConfigureWireMock(port = 0, files = "classpath:/wiremock")
// Use this runner since we want to parameterize certain tests.
// See runner's javadoc for more usage.
@RunWith(JUnitParamsRunner.class)
public class IexRestControllerTest extends ASpringTest {

  @Autowired
  private MockMvc mvc;

  @Test
  public void testGetSymbolsEndpoint() throws Exception {
    MvcResult result = this.mvc.perform(
        // note that we were are testing the fuse REST end point here, not the IEX end point.
        // the fuse end point in turn calls the IEX end point, which is WireMocked for this test.
        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/iex/symbols")
            .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        // some simple validations, in practice I would expect these to be much more comprehensive.
        .andExpect(jsonPath("$[0].symbol", is("A")))
        .andExpect(jsonPath("$[1].symbol", is("AA")))
        .andExpect(jsonPath("$[2].symbol", is("AAAU")))
        .andReturn();
  }

  @Test
  public void testGetLastTradedPrice() throws Exception {

    MvcResult result = this.mvc.perform(
        org.springframework.test.web.servlet.request.MockMvcRequestBuilders
            .get("/iex/lastTradedPrice?symbols=FB")
            // This URL will be hit by the MockMvc client. The result is configured in the file
            // src/test/resources/wiremock/mappings/mapping-lastTradedPrice.json
            .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].symbol", is("FB")))
        .andExpect(jsonPath("$[0].price").value(new BigDecimal("186.34")))
        .andReturn();
  }

  @Test
  public void testGetLastTradedPriceEmpty() throws Exception {

    MvcResult result = this.mvc.perform(
        org.springframework.test.web.servlet.request.MockMvcRequestBuilders
            .get("/iex/lastTradedPrice?symbols=")
            .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", is(Collections.emptyList())))
        .andReturn();
  }

  @Test
  public void testGetHistoricalPrices() throws Exception {

    MvcResult result = this.mvc.perform(
        org.springframework.test.web.servlet.request.MockMvcRequestBuilders
            .get("/iex/historicalPrices?symbol=COIN&range=2m&date=")
            // This URL will be hit by the MockMvc client. The result is configured in the file
            // src/test/resources/wiremock/mappings/mapping-lastTradedPrice.json
            .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].close").value(new BigDecimal("224.54")))
        .andExpect(jsonPath("$[0].high").value(new BigDecimal("227.82")))
        .andExpect(jsonPath("$[0].low").value(new BigDecimal("223.22")))
        .andExpect(jsonPath("$[0].open").value(new BigDecimal("226.97")))
        .andExpect(jsonPath("$[0].symbol", is("COIN")))
        .andExpect(jsonPath("$[0].volume").value(new BigDecimal("2506957")))
        .andExpect(jsonPath("$[0].date", is("2021-06-25")))
        .andExpect(jsonPath("$[1].close").value(new BigDecimal("246.69")))
        .andExpect(jsonPath("$[1].high").value(new BigDecimal("248.06")))
        .andExpect(jsonPath("$[1].low").value(new BigDecimal("226.13")))
        .andExpect(jsonPath("$[1].open").value(new BigDecimal("227.64")))
        .andExpect(jsonPath("$[1].symbol", is("COIN")))
        .andExpect(jsonPath("$[1].volume").value(new BigDecimal("7449035")))
        .andExpect(jsonPath("$[1].date", is("2021-06-28")))
        .andExpect(jsonPath("$[2].close").value(new BigDecimal("254.9")))
        .andExpect(jsonPath("$[2].high").value(new BigDecimal("261.15")))
        .andExpect(jsonPath("$[2].low").value(new BigDecimal("250.01")))
        .andExpect(jsonPath("$[2].open").value(new BigDecimal("250.2")))
        .andExpect(jsonPath("$[2].symbol", is("COIN")))
        .andExpect(jsonPath("$[2].volume").value(new BigDecimal("7867552")))
        .andExpect(jsonPath("$[2].date", is("2021-06-29")))
        .andExpect(jsonPath("$[3].close").value(new BigDecimal("253.3")))
        .andExpect(jsonPath("$[3].high").value(new BigDecimal("254.67")))
        .andExpect(jsonPath("$[3].low").value(new BigDecimal("247")))
        .andExpect(jsonPath("$[3].open").value(new BigDecimal("248.3")))
        .andExpect(jsonPath("$[3].symbol", is("COIN")))
        .andExpect(jsonPath("$[3].volume").value(new BigDecimal("4143487")))
        .andExpect(jsonPath("$[3].date", is("2021-06-30")))
        .andReturn();
  }

  @Test
  public void testGetHistoricalDates() throws Exception {

    MvcResult result = this.mvc.perform(
        org.springframework.test.web.servlet.request.MockMvcRequestBuilders
            .get("/iex/historicalPrices?symbol=COIN&range=2m&date=20210615")
            // This URL will be hit by the MockMvc client. The result is configured in the file
            // src/test/resources/wiremock/mappings/mapping-lastTradedPrice.json
            .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].close").value(new BigDecimal("231.99")))
        .andExpect(jsonPath("$[0].high").value(new BigDecimal("232.31")))
        .andExpect(jsonPath("$[0].low").value(new BigDecimal("231.99")))
        .andExpect(jsonPath("$[0].open").value(new BigDecimal("232.31")))
        .andExpect(jsonPath("$[0].volume").value(new BigDecimal("52")))
        .andExpect(jsonPath("$[0].date", is("2021-06-15")))
        .andExpect(jsonPath("$[1].close").value(new BigDecimal("232.45")))
        .andExpect(jsonPath("$[1].high").value(new BigDecimal("232.48")))
        .andExpect(jsonPath("$[1].low").value(new BigDecimal("232.04")))
        .andExpect(jsonPath("$[1].open").value(new BigDecimal("232.05")))
        .andExpect(jsonPath("$[1].volume").value(new BigDecimal("191")))
        .andExpect(jsonPath("$[1].date", is("2021-06-15")))
        .andExpect(jsonPath("$[2].close").value(new BigDecimal("232.02")))
        .andExpect(jsonPath("$[2].high").value(new BigDecimal("232.29")))
        .andExpect(jsonPath("$[2].low").value(new BigDecimal("232.02")))
        .andExpect(jsonPath("$[2].open").value(new BigDecimal("232.29")))
        .andExpect(jsonPath("$[2].volume").value(new BigDecimal("213")))
        .andExpect(jsonPath("$[2].date", is("2021-06-15")))
        .andExpect(jsonPath("$[3].close").value(new BigDecimal("232.17")))
        .andExpect(jsonPath("$[3].high").value(new BigDecimal("232.345")))
        .andExpect(jsonPath("$[3].low").value(new BigDecimal("231.93")))
        .andExpect(jsonPath("$[3].open").value(new BigDecimal("232.1")))
        .andExpect(jsonPath("$[3].volume").value(new BigDecimal("1383")))
        .andExpect(jsonPath("$[3].date", is("2021-06-15")))
        .andReturn();
  }
}
