package com.codeburl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.xmlcalabash.drivers.Main;
//import io.transpect.calabash.extensions.UnZip;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;



public class IDMLHandler {
  public void handleEvent(S3Put event) throws IOException {
    final String key = event.Records.get(0).s3.object.key;
    System.out.println("S3 PUT happened! Key was: " + key);

    String catalogs = System.getProperty("xml.catalog.files");
    System.out.println("XML Catalogs property set to: " + catalogs);

		String bucket_name = "codeburldev.com-files";
		String key_name = "hello-world.idml";
		String output_filename = "/tmp/hello-world.idml";

    final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                          .withRegion(Regions.US_EAST_1)
                          .build();
		try {
			S3Object o = s3.getObject(bucket_name, key_name);
			S3ObjectInputStream s3is = o.getObjectContent();
			FileOutputStream fos = new FileOutputStream(new File(output_filename));
			byte[] read_buf = new byte[1024];
			int read_len = 0;
			while ((read_len = s3is.read(read_buf)) > 0) {
				fos.write(read_buf, 0, read_len);
			}
			s3is.close();
			fos.close();
		} catch (AmazonServiceException e) {
			System.err.println(e.getErrorMessage());
			System.exit(1);
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		System.out.println("Done!");

		final String[] args = new String[6];
		args[0] = "-o result=/tmp/output.xml";
		args[1] = "--config=transpect-config.xml";
		//args[1] = "--config=calabash/extensions/transpect/transpect-config.xml";
		args[2] = "xpl/idml2xml-frontend.xpl";
		args[3] = "idml=/tmp/hello-world.idml";
		args[4] = "debug=yes";
		args[5] = "status-dir-uri=/tmp";

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
