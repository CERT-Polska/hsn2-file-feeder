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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import org.apache.commons.httpclient.URIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.ParameterException;
import pl.nask.hsn2.RequiredParameterMissingException;
import pl.nask.hsn2.ResourceException;
import pl.nask.hsn2.StorageException;
import pl.nask.hsn2.TaskContext;
import pl.nask.hsn2.task.Task;
import pl.nask.hsn2.wrappers.ParametersWrapper;

public class FileFeederTask implements Task {
    private static final Logger LOG = LoggerFactory.getLogger(FileFeederTask.class);

    private final TaskContext jobContext;

    // job parameters
    private String uri;
    // undocumented feature - if url is passed, it shall be used instead of uri
    private String url;
    private boolean makeStats;
    private boolean addDomainInfo;

    private StatsCollector statsCollector;


    public FileFeederTask(TaskContext jobContext, ParametersWrapper params) throws RequiredParameterMissingException {
        this.jobContext = jobContext;
        applyParameters(params);
        statsCollector = new StatsCollector(makeStats);
    }

    private void applyParameters(ParametersWrapper params) throws RequiredParameterMissingException {
        if (params.hasParam("url")) {
            url = params.get("url");
        } else {
            uri = params.get("uri");
        }

        makeStats = params.getBoolean("statistics", true);
        addDomainInfo = params.getBoolean("domain_info", true);
    }

    public final boolean takesMuchTime() {
        return true;
    }

    public final void process() throws ParameterException, ResourceException, StorageException {
        statsCollector.collectingStarted();

        try {
            if (url != null) {
                processStringUrl(url);
            } else {
                processWithUri();
            }
        } finally {
            statsCollector.collectingEnded();
            publishStatistics();
        }
    }

    private void processWithUri() throws ResourceException, StorageException {
        LineNumberReader reader = openReader();

        try {
            String line;
            while ((line = reader.readLine()) != null) {
            	String lineTrimmed = line.trim();
            	if (lineTrimmed.length() != 0) {
            		processStringUrl(lineTrimmed);
            	}
            }
        } catch (IOException e) {
            throw new ResourceException("Error reading from file: " + uri, e);
        } finally {
            closeReader(reader);
        }
    }

    private void closeReader(LineNumberReader reader) {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            LOG.warn("Error closing file", e);
        }
    }

    private void publishStatistics() {
        jobContext.addTimeAttribute("feeder_time_begin", statsCollector.getStartTime());
        jobContext.addTimeAttribute("feeder_time_end", statsCollector.getEndTime());
        jobContext.addAttribute("url_count", statsCollector.getUrlCount());
        if (makeStats) {
            jobContext.addAttribute("domain_count", statsCollector.getDomainCount());
            jobContext.addAttribute("domain_idn_count", statsCollector.getDomainIdnCount());
            jobContext.addAttribute("ipv4_count", statsCollector.getIPv4Count());
            jobContext.addAttribute("ipv6_count", statsCollector.getIPv6Count());
        }
    }

    private void processStringUrl(String url) throws StorageException {
        try {
        	NewFileFeederUrlObject newFileFeederObject = new NewFileFeederUrlObject(url, "file", addDomainInfo);
            jobContext.newObject(newFileFeederObject);
            statsCollector.collectStats(newFileFeederObject);
        } catch (URIException e) {
            LOG.warn("Not an URL!: {}, msg={}", url, e.getMessage());
        }
    }

    private LineNumberReader openReader() throws ResourceException {
        FileReader fileReader;
        try {
            fileReader = new FileReader(uri);
        } catch (FileNotFoundException e) {
            throw new ResourceException(String.format("No file with name=%s found", uri), e);
        }
        LineNumberReader reader = new LineNumberReader(fileReader);

        return reader;
    }
}
