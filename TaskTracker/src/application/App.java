package application;

import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;

import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;

import javafx.collections.ObservableList;

import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;

import java.util.*;
import java.time.*;

public class App {
	static MongoClient mongoClient = MongoClients.create(
			"mongodb+srv://newUser427:LeocXHZ9L99jZQ16@cluster0.uo7qm.mongodb.net/myFirstDatabase?retryWrites=true&w=majority");
	static MongoDatabase db = mongoClient.getDatabase("all_tasks");
	static MongoCollection<Document> collection = db.getCollection("test_tasks");

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// examples from mongodb docs
		String connectionString = "mongodb+srv://newUser427:LeocXHZ9L99jZQ16@cluster0.uo7qm.mongodb.net/myFirstDatabase?retryWrites=true&w=majority";

		try (MongoClient mongoClient = MongoClients.create(connectionString)) {

			MongoIterable<String> strings = mongoClient.listDatabaseNames();
			MongoCursor<String> cursor = strings.cursor();
			while (cursor.hasNext()) {
				System.out.println(cursor.next());
			}

			MongoDatabase db = mongoClient.getDatabase("tasks");

			try {
//				MongoCollection<Document> collection = db.getCollection();

				MongoCollection<Document> collection = db.getCollection("test_tasks");
//				db.createCollection("test");
				
				
				// prints out collections in 'case' db
				MongoCursor<String> it = db.listCollectionNames().iterator();
				System.out.println("collections in the " + db.getName() + " database");
				while (it.hasNext()) {
					System.out.println("\t" + it.next());
				}
				System.out.println("Connected successfully to server.");

				// see the data
				Document doc = collection.find(Filters.eq("name", "Paul Zapote")).first();
				if (doc != null) {
					System.out.println("_id: " + doc.getObjectId("_id") + ", name: " + doc.getString("name")
							+ ", dateOfDeath: " + doc.getDate("dateOfDeath"));

					doc.getList("novels", Document.class).forEach((novel) -> {
						System.out.println("title: " + novel.getString("title") + ", yearPublished: "
								+ novel.getInteger("yearPublished"));
					});
					Collection<Object> map = doc.values();
					for (Object e : map) {
						System.out.println(e + "\n");
					}
				}
				Bson filter = Filters.eq("date created", "2021 May 03 Tue.");
				System.out.println(collection.deleteMany(filter));
			} catch (MongoException me) {
				System.err.println("An error occurred while attempting to run a command");
			}
		}

	}

	// returns true if successfully submitted to cluster, false otherwise
	public static boolean insertTask(Task task) {
		Bson filter = Filters.and(Filters.eq("date_created", task.getDateCreated()), Filters.eq("task_name", task.getTitle()));
		if (collection.find(filter).first() == null) {
			Document doc = new Document("task_name", task.getTitle())
					.append("date_created", task.getDateCreated())
					.append("time_created", task.getTimeCreated())
					.append("total_time", task.getTotalTime())
					.append("stoppages", task.getStoppages())
					.append("stoppage_times",task.getStoppageTimesList());
			System.out.println(doc.toJson());
			try {
				InsertOneResult result = collection.insertOne(doc);
				return result.wasAcknowledged();
			} catch (Exception e) {
				throw e;
			}
		}
		return false;
	}

	public static boolean update(Task task) {
		Bson filter = Filters.and(Filters.eq("date_created", task.getDateCreated()), Filters.eq("task_name", task.getTitle()));
		Document doc = new Document("task_name", task.getTitle())
				.append("date_created", task.getDateCreated())
				.append("time_created", task.getTimeCreated())
				.append("total_time", task.getTotalTime())
				.append("stoppages", task.getStoppages())
				.append("stoppage_times", task.getStoppageTimesList());
//		Bson update = Updates.set("total_time", task.getTotalTime());
		try {
			UpdateResult result = collection.replaceOne(filter, doc);
			return result.wasAcknowledged();
		} catch (Exception e) {
			throw e;
		}
	}

	public static boolean deleteTask(Task task) {
		Bson filter = Filters.and(Filters.eq("date_created", task.getDateCreated()), Filters.eq("task_name", task.getTitle()));

		try {
			DeleteResult result = collection.deleteOne(filter);
			System.out.println(result);
			return result.wasAcknowledged();
		} catch (Exception e) {
			throw e;
		}
	}
	
	public static String findByName(String name) {
		if(name.equals("all") || name.isEmpty()) {
			MongoCursor<Document> cursor = collection.find()
					.projection(Projections.excludeId()).iterator();
			String str = "";
			while(cursor.hasNext()) {
				str += "\n" + cursor.next().toJson(JsonWriterSettings.builder().indent(true).build());
			}
			System.out.println("Returned all tasks from db");
			return str;
		}
		Bson filter = Filters.eq("task_name", name);
		try {
			String str = collection.find(filter).projection(Projections.excludeId()).first().toJson();
			System.out.println("seached for \""+ name+"\" task");
			return str;
		} 
		catch (NullPointerException e) {
			// null pointer exception if no document is found
			return "{ none found }";
		}
		catch (Exception e) {
			throw e;
		}
	}
}
