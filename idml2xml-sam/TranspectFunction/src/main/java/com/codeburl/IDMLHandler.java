package com.codeburl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.xmlcalabash.drivers.Main;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;


public class IDMLHandler {
  final Logger logger = LogManager.getLogger(IDMLHandler.class);
  final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                        .withRegion(Regions.US_EAST_1)
                        .build();

  public void handleEvent(S3Put event) throws IOException {
    final String input_bucket = event.Records.get(0).s3.bucket.name;
    final String input_key = event.Records.get(0).s3.object.key;

    logger.info("Handling S3 PUT to " + input_bucket + "/" + input_key);

		final String input_fn = "/tmp/input.idml";
    downloadInputIDML(input_bucket, input_key, input_fn);
    final String output_fn = invokeXProc(input_fn);
    logger.info("HUB XML output sent to " + output_fn);
    final String output_bucket = System.getenv("OUTPUT_BUCKET");
    final String result_arn = uploadOutputXML(output_bucket, output_fn);
  }

  private String invokeXProc(String input_fn) throws IOException {
    final String config_fn = "/var/task/transpect-config.xml";
    final String xpl_fn = "/var/task/xpl/idml2xml-frontend.xpl";
    //final String config_fn = this.getClass().getClassLoader().getResource("transpect-config.xml").toString();
    //final String xpl_fn = this.getClass().getClassLoader().getResource("xpl/idml2xml-frontend.xpl").toString();
    logger.info("Invoking XProc pipline (" + xpl_fn + ") with config " + config_fn);
    String output_fn = "/tmp/output.xml";
		final String[] args = new String[5];
		args[0] = "--config=" + config_fn;
		args[1] = "-oresult=" + output_fn;
		args[2] = xpl_fn;
		args[3] = "idml=" + input_fn;
		args[4] = "status-dir-uri=/tmp";

    Main calabash = new Main();
    calabash.run(args);
    return output_fn;
  }

  private void downloadInputIDML(String bucket, String key, String input_fn) {
		try {
			S3Object o = s3.getObject(bucket, key);
			S3ObjectInputStream s3is = o.getObjectContent();
      logger.info("Downloading IDML file from S3 (arn:aws:s3:::" + bucket + "/" + key + ") to " + input_fn);
			FileOutputStream fos = new FileOutputStream(new File(input_fn));
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
  }

  private String uploadOutputXML(String output_bucket, String output_fn) {
    UUID uuid = UUID.randomUUID();
    final String output_key = uuid.toString() + ".xml";
    final String arn = "arn:aws:s3:::" + output_bucket + "/" + output_key;
		try {
			PutObjectRequest request = new PutObjectRequest(output_bucket, output_key, new File(output_fn));
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType("text/xml");
			request.setMetadata(metadata);
      logger.info("Uploading XML file to S3 at " + arn);
			s3.putObject(request);
		} catch (AmazonServiceException e) {
			System.err.println(e.getErrorMessage());
			System.exit(1);
		}
		return arn;
  }


	public static class S3Put {
		public java.util.List<S3PutRecord> Records;

		public static class S3PutRecord {
			public S3PutRecordS3 s3;

			public static class S3PutRecordS3 {
				public S3PutRecordS3Bucket bucket;
				public S3PutRecordS3Object object;

				public static class S3PutRecordS3Bucket {
          public String name;
				}

				public static class S3PutRecordS3Object {
					public String key;
				}
			}
		}
	}
}
