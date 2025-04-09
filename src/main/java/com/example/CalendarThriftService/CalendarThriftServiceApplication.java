package com.example.CalendarThriftService;

import com.example.CalendarThriftService.generated.MeetingManage;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class CalendarThriftServiceApplication {

	private static final Logger logger = LoggerFactory.getLogger(CalendarThriftServiceApplication.class);

	public static void startserver(MeetingServiceHandler meetingServiceHandler) throws TTransportException{
		try {
			TServerTransport serverTransport = new TServerSocket(9090);
			MeetingManage.Processor<MeetingServiceHandler> processor =
					new MeetingManage.Processor<>(meetingServiceHandler);


			TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));

			logger.info("Starting the Calendar Thrift Service on port 9090...");

			server.serve();
		} catch (TTransportException e) {
			throw new RuntimeException("Error starting Thrift server", e);
		}
    }

	public static void main(String[] args) throws TTransportException {

		ConfigurableApplicationContext applicationContext = SpringApplication.run(CalendarThriftServiceApplication.class, args);
		startserver(applicationContext.getBean(MeetingServiceHandler.class));
	}

}
