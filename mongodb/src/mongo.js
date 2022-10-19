import { MongoClient } from "mongodb";

let client;

function initClient(url) {
  client = new MongoClient(url);
}

async function find(query) {
  const { dbName, dbCollection } = $jsapi.context().injector;

  try {
    await client.connect();
    const collection = client.db(dbName).collection(dbCollection);

    const result = await collection
      .find(query ? { input: query } : {})
      .toArray();

    await client.close();
    return result;
  } catch (error) {
    $reactions.answer(JSON.stringify(error));
  }
}

async function insert(query) {
  const { dbName, dbCollection } = $jsapi.context().injector;

  try {
    await client.connect();
    const collection = client.db(dbName).collection(dbCollection);

    const result = await collection.insertOne({
      input: query,
      datetime: new Date().toString(),
    });

    await client.close();
    return result.insertedId;
  } catch (error) {
    $reactions.answer(JSON.stringify(error));
  }
}

export default {
  initClient,
  find,
  insert,
};
