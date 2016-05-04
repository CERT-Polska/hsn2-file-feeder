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

import org.apache.commons.httpclient.URIException;

import pl.nask.hsn2.NewUrlObject;
import pl.nask.hsn2.bus.operations.ObjectData;
import pl.nask.hsn2.bus.operations.builder.ObjectDataBuilder;

public class NewFileFeederUrlObject extends NewUrlObject {
	private boolean addDomainInfo;

	public NewFileFeederUrlObject(String url, String origin, boolean addDomainInfo) throws URIException {
		super(url, origin, null);
		this.addDomainInfo = addDomainInfo;
	}

	// FileFeeder does not add a reference to parent
	@Override
	public final ObjectData asDataObject(Long parentId) {
		ObjectDataBuilder objectBuilder = new ObjectDataBuilder();
		objectBuilder.addStringAttribute("type", "url");
		objectBuilder.addStringAttribute("url_original", getOriginalUrl());
		objectBuilder.addTimeAttribute("creation_time", System.currentTimeMillis());
		objectBuilder.addStringAttribute("origin", getOrigin());

		if (getType() != null)
			objectBuilder.addStringAttribute("type", getType());

		if (addDomainInfo) {
			if (getDomain() != null) {
				objectBuilder.addStringAttribute("url_domain", getDomain());
			} else if (getIp() != null) {
				objectBuilder.addStringAttribute("url_ip", getIp());
			}
		}
		return objectBuilder.build();
	}
}
