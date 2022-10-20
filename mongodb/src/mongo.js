import { MongoClient } from "mongodb";

let client;

function initClient() {
  if (client) {
    return; // Skip if already initialized
  }
  client = new MongoClient($secrets.get("mongoDbConnectionString"));
}

async function find(query) {
  await client.connect();
  const collection = client
    .db($env.get("dbName"))
    .collection($env.get("dbCollection"));

  const result = await collection.find(query ? { query } : {}).toArray();

  await client.close();
  return result;
}

async function insert(query) {
  await client.connect();
  const collection = client
    .db($env.get("dbName"))
    .collection($env.get("dbCollection"));

  const result = await collection.insertOne({
    query,
    datetime: new Date().toString(),
  });

  await client.close();
  return result.insertedId;
}

export default {
  initClient,
  find,
  insert,
};
