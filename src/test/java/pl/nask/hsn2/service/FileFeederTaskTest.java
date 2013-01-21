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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import mockit.Mocked;
import mockit.NonStrictExpectations;

import org.testng.annotations.Test;

import pl.nask.hsn2.ServiceConnector;
import pl.nask.hsn2.TaskContext;
import pl.nask.hsn2.bus.operations.ObjectResponse.ResponseType;
import pl.nask.hsn2.bus.operations.builder.ObjectResponseBuilder;
import pl.nask.hsn2.protobuff.Object.ObjectData;
import pl.nask.hsn2.wrappers.ParametersWrapper;

public class FileFeederTaskTest {
	@Mocked
	ServiceConnector serviceConnector;
	
	@Test
	public void testCheckHandlingOfEmptyLines() throws Exception {

		Map<String, String> map = new HashMap<String, String>();
		// file contains empty lines and two urls
		map.put("uri", "file.txt");
		ParametersWrapper params = new ParametersWrapper(map);
		
		TaskContext ctx = new TaskContext(1, 1, 1, serviceConnector);
		FileFeederTask task = new FileFeederTask(ctx , params );
		
		new NonStrictExpectations() {{ 
			serviceConnector.saveObjects(anyLong, withInstanceOf(List.class));
			forEachInvocation = new Object() {
				public void validate(long id, List<ObjectData> list) {
					Assert.assertEquals("number of objects to be added", 2, list.size());
				}
			};
			result=new ObjectResponseBuilder(ResponseType.SUCCESS_PUT).addObject(0).build();
		}};
		
		task.process();
		List<Long> list = ctx.getAddedObjects();
		ctx.flush();
	}
}
