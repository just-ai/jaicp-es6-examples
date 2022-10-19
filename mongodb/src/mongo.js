import { MongoClient } from "mongodb";

let client;

function initClient(url) {
  if (client) {
    return; // Skip if already initialized
  }
  client = new MongoClient(url);
}

async function find(query) {
  const { dbName, dbCollection } = $jsapi.context().injector;
  await client.connect();
  const collection = client.db(dbName).collection(dbCollection);

  const result = await collection.find(query ? { input: query } : {}).toArray();

  await client.close();
  return result;
}

async function insert(query) {
  const { dbName, dbCollection } = $jsapi.context().injector;
  await client.connect();
  const collection = client.db(dbName).collection(dbCollection);

  const result = await collection.insertOne({
    input: query,
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
