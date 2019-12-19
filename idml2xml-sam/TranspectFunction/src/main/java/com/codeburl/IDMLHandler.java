package com.codeburl;

import java.io.IOException;
import com.xmlcalabash.drivers.Main;

public class IDMLHandler {
  public void handleEvent(S3Put event) throws IOException {
    final String key = event.Records.get(0).s3.object.key;
    final String[] args = new String[1];
		args[0] = "xpl/idml2xml-frontend.xpl";

    System.out.println("S3 PUT happened! Key was: " + key);
    Main calabash = new Main();
    calabash.run(args);
  }

	public static class S3Put {
		public java.util.List<S3PutRecord> Records;

		public static class S3PutRecord {
			public S3PutRecordS3 s3;

			public static class S3PutRecordS3 {
				public S3PutRecordS3Object object;

				public static class S3PutRecordS3Object {
					public String key;
				}
			}
		}
	}
}
