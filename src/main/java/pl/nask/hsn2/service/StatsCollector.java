/*
 * Copyright (c) NASK, NCSC
 *
 * This file is part of HoneySpider Network 2.0.
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

import pl.nask.hsn2.NewUrlObject;

public class StatsCollector {

    private static final String COLLECTING_ENDED_MSG = "Stats collecting already ended (collectingEnded() was called";
    private boolean makeStats;
    private int urlCount;
    private long endTime;
    private long startTime;
    private int domainCount;
    private int domainIdnCount;
    private int ipv4Count;
    private int ipv6Count;

    public StatsCollector(boolean makeStats) {
        this.makeStats = makeStats;
    }

    public final int getUrlCount() {
        return urlCount;
    }

    public final void collectStats(NewUrlObject newObject) {
        if (endTime != 0) {
            throw new IllegalStateException(COLLECTING_ENDED_MSG);
        }

        if (startTime == 0) {
            collectingStarted();
        }

        urlCount++;
        if (makeStats)
            doCollectStats(newObject);
    }

    private void doCollectStats(NewUrlObject newObject) {
        if (newObject.getDomain() != null) {
            domainCount ++;
            if (newObject.usesIDN()) {
                domainIdnCount++;
            }
        } else {
            // IP address is used
            if (newObject.usesIPv4()) {
               ipv4Count++;
            } else if (newObject.usesIPv6()) {
                ipv6Count++;
            }
        }
    }

    public final int getDomainCount() {
        return domainCount;
    }

    public final int getDomainIdnCount() {
        return domainIdnCount;
    }

    public final int getIPv4Count() {
        return ipv4Count;
    }

    public final int getIPv6Count() {
        return ipv6Count;
    }

    public final void collectingEnded() {
        endTime = System.currentTimeMillis();
    }

    public final void collectingStarted() {
        if (endTime != 0) {
            throw new IllegalStateException(COLLECTING_ENDED_MSG);
        }
        startTime = System.currentTimeMillis();
    }

    public final long getStartTime() {
        return startTime;
    }

    public final long getEndTime() {
        return endTime;
    }
}
