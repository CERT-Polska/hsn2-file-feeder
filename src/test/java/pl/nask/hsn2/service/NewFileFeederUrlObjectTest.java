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

import pl.nask.hsn2.bus.operations.AttributeType;
import pl.nask.hsn2.bus.operations.ObjectData;

public class NewFileFeederUrlObjectTest {
	private NewFileFeederUrlObject obj;
	
	@BeforeTest
	public void init() throws URIException {
		obj = new NewFileFeederUrlObject("http://nask.pl/", "file", true);
	}
	
	// FileFeeder must not add a reference to the parent DataObject
	@Test
	public void testParentAttribute() throws URIException {	
		ObjectData dataObj = obj.asDataObject(1L);
		Assert.assertNull("'parent' attribute", dataObj.findAttribute("parent", AttributeType.OBJECT));
	}
		
}
