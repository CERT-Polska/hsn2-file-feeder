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

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonController;
import org.apache.commons.daemon.DaemonInitException;

import pl.nask.hsn2.CommandLineParams;
import pl.nask.hsn2.GenericService;

public final class FileFeederService implements Daemon{
	private Thread serviceRunner = null;
	private CommandLineParams cmd = null;

	public static void main(final String[] args) throws DaemonInitException, Exception {
		FileFeederService ffs = new FileFeederService();
		ffs.init(new DaemonContext() {
			
			@Override
			public DaemonController getController() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String[] getArguments() {
				return args;
			}
		});
		ffs.start();
		ffs.serviceRunner.join();
		ffs.stop();
		ffs.destroy();
	}

	@Override
	public void init(DaemonContext context) throws DaemonInitException,
			Exception {
		cmd = new CommandLineParams();
		cmd.useDataStoreAddressOption(false);
		cmd.setDefaultServiceNameAndQueueName("feeder-list");

		cmd.parseParams(context.getArguments());
		final GenericService service = new GenericService(new FileFeederTaskFactory(), cmd.getMaxThreads(), cmd.getRbtCommonExchangeName(), cmd.getRbtNotifyExchangeName());
		cmd.applyArguments(service);

		serviceRunner = new Thread(new Runnable() {
			
			@Override
			public void run() {
				Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {			
					@Override
					public void uncaughtException(Thread t, Throwable e) {
						System.exit(1);	
					}
				});
				try {
					service.run();
				} catch (InterruptedException e1) {
					System.exit(1);
				}
				
			}
		},"file-feeder-Service");
		
	}

	@Override
	public void start() throws Exception {
		serviceRunner.start();
	}

	@Override
	public void stop() throws Exception {
		if ( serviceRunner == null)
			return;
		serviceRunner.interrupt();
		serviceRunner.join(10000);
		
	}

	@Override
	public void destroy() {
		serviceRunner = null;
		
	}
}
