/*
 * Copyright (c) NASK, NCSC
 * 
 * This file is part of HoneySpider Network 2.1.
 * 
 * This is a free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.nask.hsn2.service;

import junit.framework.Assert;

import org.apache.commons.httpclient.URIException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import pl.nask.hsn2.NewUrlObject;

public class StatsCollectorTest {

    private NewUrlObject domainUrl;
    private NewUrlObject ip4Url;
    private NewUrlObject ip6Url;
    private NewUrlObject domainIdnUrl;

    @BeforeTest
    public void beforeTest() throws URIException {
        domainUrl = new NewUrlObject("http://nask.pl", "test");
        ip4Url = new NewUrlObject("http://127.0.0.1/", "test");
        ip6Url = new NewUrlObject("http://[0:0:0:0:0:0:0:1]/", "test");
        domainIdnUrl = new NewUrlObject("http://ąę.pl/", "test");
    }

    @Test
    public void testMakeNoStats() {
        StatsCollector collector = new StatsCollector(false);
        collector.collectingStarted();
        collector.collectStats(domainUrl);
        collector.collectingEnded();

        commonCheck(collector, 1);

        Assert.assertTrue(collector.getStartTime() != 0);
        Assert.assertTrue(collector.getEndTime() != 0);

        Assert.assertEquals(0, collector.getDomainCount());
        Assert.assertEquals(0, collector.getDomainIdnCount());
        Assert.assertEquals(0, collector.getIPv4Count());
        Assert.assertEquals(0, collector.getIPv6Count());
    }

    private void commonCheck(StatsCollector collector, int i) {
     // urls processed
        Assert.assertEquals(i, collector.getUrlCount());
        // start/end time updated
        Assert.assertTrue(collector.getStartTime() != 0);
        Assert.assertTrue(collector.getEndTime() != 0);
    }

    @Test
    public void testMakeStatsForDomain() {
        StatsCollector collector = new StatsCollector(true);
        collector.collectingStarted();
        collector.collectStats(domainUrl);
        collector.collectingEnded();

        commonCheck(collector, 1);
        Assert.assertEquals(1, collector.getDomainCount());
        Assert.assertEquals(0, collector.getDomainIdnCount());
        Assert.assertEquals(0, collector.getIPv4Count());
        Assert.assertEquals(0, collector.getIPv6Count());
    }

    @Test
    public void testMakeStatsForIdnDomain() {
        StatsCollector collector = new StatsCollector(true);
        collector.collectingStarted();
        collector.collectStats(domainIdnUrl);
        collector.collectingEnded();

        commonCheck(collector, 1);
        Assert.assertEquals(1, collector.getDomainCount());
        Assert.assertEquals(1, collector.getDomainIdnCount());
        Assert.assertEquals(0, collector.getIPv4Count());
        Assert.assertEquals(0, collector.getIPv6Count());
    }

    @Test
    public void testMakeStatsForIPv4() {
        StatsCollector collector = new StatsCollector(true);
        collector.collectingStarted();
        collector.collectStats(ip4Url);
        collector.collectingEnded();

        commonCheck(collector, 1);
        Assert.assertEquals(0, collector.getDomainCount());
        Assert.assertEquals(0, collector.getDomainIdnCount());
        Assert.assertEquals(1, collector.getIPv4Count());
        Assert.assertEquals(0, collector.getIPv6Count());
    }

    @Test
    public void testMakeStatsForIPv6() {
        StatsCollector collector = new StatsCollector(true);
        collector.collectingStarted();
        collector.collectStats(ip6Url);
        collector.collectingEnded();

        commonCheck(collector, 1);
        Assert.assertEquals(0, collector.getDomainCount());
        Assert.assertEquals(0, collector.getDomainIdnCount());
        Assert.assertEquals(0, collector.getIPv4Count());
        Assert.assertEquals(1, collector.getIPv6Count());
    }

    @Test
    public void testMakeStatsForAll() {
        StatsCollector collector = new StatsCollector(true);
        collector.collectingStarted();
        collector.collectStats(ip6Url);
        collector.collectStats(ip4Url);
        collector.collectStats(domainUrl);
        collector.collectStats(domainIdnUrl);
        collector.collectingEnded();
        commonCheck(collector, 4);

        Assert.assertEquals(2, collector.getDomainCount());
        Assert.assertEquals(1, collector.getDomainIdnCount());
        Assert.assertEquals(1, collector.getIPv4Count());
        Assert.assertEquals(1, collector.getIPv6Count());
    }

    @Test(expectedExceptions=IllegalStateException.class)
    public void testUpdateTimeWrongOrder() {
        StatsCollector collector = new StatsCollector(true);
        collector.collectingEnded();
        collector.collectingStarted();
    }

    @Test
    public void testUpdateTime() throws InterruptedException {
        StatsCollector collector = new StatsCollector(true);
        collector.collectingStarted();
        Thread.sleep(10);
        collector.collectingEnded();
        Assert.assertTrue(collector.getStartTime() < collector.getEndTime());
    }

    @Test
    public void testLazyStartTimeUpdate() throws InterruptedException {
        StatsCollector collector = new StatsCollector(true);
        collector.collectStats(domainUrl);
        Thread.sleep(10);
        collector.collectingEnded();
        Assert.assertTrue(collector.getStartTime() != 0);
        Assert.assertTrue(collector.getStartTime() < collector.getEndTime());
    }

    @Test(expectedExceptions=IllegalStateException.class)
    public void testCollectAfterEnd() throws InterruptedException {
        StatsCollector collector = new StatsCollector(true);
        collector.collectingEnded();
        collector.collectStats(domainUrl);
    }
}
