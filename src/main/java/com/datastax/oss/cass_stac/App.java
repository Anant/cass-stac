package com.datastax.oss.cass_stac;

import com.datastax.oss.cass_stac.config.DataStaxAstraProperties;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.type.codec.registry.MutableCodecRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(DataStaxAstraProperties.class)
@RequiredArgsConstructor
@Slf4j
public class App implements CommandLineRunner{
	
	private final CqlSessionBuilder cqlSessionBuilder;

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
                public void run() {
                    System.out.println("Shutting down the application");
                }
		}));
	}

	@Override
	public void run(String... args) throws Exception {
		
		
		final String item_create_table_statement = """
					CREATE TABLE IF NOT EXISTS item (
					    partition_id text,
					    id text,
					    additional_attributes text,
					    centroid vector<float, 2>,
					    collection text,
					    datetime timestamp,
					    geometry blob,
					    indexed_properties_boolean map<text, boolean>,
					    indexed_properties_double map<text, double>,
					    indexed_properties_text map<text, text>,
					    indexed_properties_timestamp map<text, timestamp>,
					    properties text,
					    PRIMARY KEY (partition_id, id))
				""";
		final String itemids_create_table_statement = """
				CREATE TABLE IF NOT EXISTS item_ids (
				    id text PRIMARY KEY,
				    datetime timestamp,
				    partition_id text)
			""";
		final String feature_create_table_statement = """
				CREATE TABLE IF NOT EXISTS feature (
					partition_id text,
				    item_id text,
				    label text,
				    datetime timestamp,
				    centroid vector<float, 2>,
				    additional_attributes text,
				    geometry blob,
				    indexed_properties_boolean map<text, boolean>,
				    indexed_properties_double map<text, double>,
				    indexed_properties_text map<text, text>,
				    indexed_properties_timestamp map<text, timestamp>,
				    properties text,
				    PRIMARY KEY ((partition_id, item_id), label, datetime, centroid))
				""";
		final String feature_collection_create_table_statement = """
				CREATE TABLE IF NOT EXISTS feature_collection (
					item_id text PRIMARY KEY,
				    additional_attributes text,
				    partition_id text,
				    properties text)
				""";
		
		final String item_datetime_create_index_statement = """
				CREATE CUSTOM INDEX IF NOT EXISTS item_datetime ON item (datetime) USING 'StorageAttachedIndex'
				""";
		final String item_properties_datetime_create_index_statement = """
				CREATE CUSTOM INDEX IF NOT EXISTS item_properties_timestamp_entries ON item (entries(indexed_properties_timestamp)) USING 'StorageAttachedIndex'
				""";
		final String item_centroid_create_index_statement = """
				CREATE CUSTOM INDEX IF NOT EXISTS item_centroid_ann_idx ON item (centroid) USING 'org.apache.cassandra.index.sai.StorageAttachedIndex' WITH OPTIONS = {'similarity_function': 'euclidean'}
				""";

		CqlSession cqlSession = cqlSessionBuilder.build();

		// Execute for Table
		try {
			cqlSession.execute(item_create_table_statement);
			log.info("Verification of Item table is successful");
			cqlSession.execute(itemids_create_table_statement);
			log.info("Verification of Item_ids table is successful");
			cqlSession.execute(feature_create_table_statement);
			log.info("Verification of Feature table is successful");
			cqlSession.execute(feature_collection_create_table_statement);
			log.info("Verification of Feature_collection table is successful");
			
			// Execute for Indexes
			cqlSession.execute(item_datetime_create_index_statement);
			log.info("Verification of Item_datetime index is successful");
			cqlSession.execute(item_properties_datetime_create_index_statement);
			log.info("Verification of Item_properties_datetime index is successful");
			cqlSession.execute(item_centroid_create_index_statement);
			log.info("Verification of Item_centroid index is successful");
		} catch(Exception ex) {
			log.error(ex.getLocalizedMessage());
			System.exit(1);
		}
		
	}

}
