package application;

import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;
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
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;

import java.util.*;
import java.time.*;

public class App {
	static MongoClient mongoClient = MongoClients.create("mongodb+srv://casetrackerUser:superSafe@cluster0.uo7qm.mongodb.net/myFirstDatabase?retryWrites=true&w=majority");
	static MongoDatabase db = mongoClient.getDatabase("case_tracker");
	static MongoCollection<Document> collection = db.getCollection("test_cases");

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String connectionString = "mongodb+srv://casetrackerUser:superSafe@cluster0.uo7qm.mongodb.net/myFirstDatabase?retryWrites=true&w=majority";

		try (MongoClient mongoClient = MongoClients.create(connectionString)) {

			MongoIterable<String> strings = mongoClient.listDatabaseNames();
			MongoCursor<String> cursor = strings.cursor();
			while (cursor.hasNext()) {
				System.out.println(cursor.next());
			}

			MongoDatabase db = mongoClient.getDatabase("case_tracker");

			try {
//				MongoCollection<Document> collection = db.getCollection();

				MongoCollection<Document> collection = db.getCollection("case");
//				db.createCollection("test");

				// prints out collections in 'case' db
				MongoCursor<String> it = db.listCollectionNames().iterator();
				System.out.println("collections in the " + db.getName() + " database");
				while (it.hasNext()) {
					System.out.println("\t" + it.next());
				}
				System.out.println("Connected successfully to server.");

				Bson filter = Filters.eq("name", "Paul Zapote");
				collection.find(filter).forEach(doc -> System.out.println(doc.toJson()));

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
			} catch (MongoException me) {
				System.err.println("An error occurred while attempting to run a command");
			}
		}

	}

	// returns true if successfully submitted to cluster, false otherwise
	public static boolean insertTask(Task task) {
		Bson filter = Filters.eq("taskName", task.getTitle());
		if(collection.find(filter).first() == null) {
			Document doc = new Document("taskName", task.getTitle()).append("date created", task.getDateTime())
					.append("total time", task.getTotalTime());
			System.out.println(doc.toJson());
			try {
				InsertOneResult result = collection.insertOne(doc);
				return true;
			} catch (Exception e) {
				throw e;
			}
		}
		return false;
	}
	
	public static String findByName(String name) {
		Bson filter = Filters.eq("taskName", name);
		try {
		String str = collection.find(filter).projection(Projections.excludeId()).first().toJson();
			return str;
		}
		catch(NullPointerException e) {
			return "{}";
		}
	}
	
	public static boolean update(Task task) {
		Bson filter = Filters.eq("taskName", task.getTitle());
		Document doc = new Document("taskName", task.getTitle())
				.append("date created", task.getDateTime())
				.append("total time", task.getTotalTime());
		UpdateResult result = collection.replaceOne(filter, doc);
		if(result.getMatchedCount() == 1)
			return true;
		else {
			System.out.println(result);
			return false;
		}
	}
}
