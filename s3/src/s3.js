import {
  S3Client,
  GetObjectCommand,
  ListBucketsCommand,
  ListObjectsV2Command,
} from "@aws-sdk/client-s3";
import utils from "./utils.js";

let client;

function initClient(accessKeyId, secretAccessKey) {
  if (client) {
    return; // Skip if already initialized
  }
  client = new S3Client({
    credentials: { accessKeyId, secretAccessKey },
    endpoint: $injector.endpoint,
    s3ForcePathStyle: true,
    region: $injector.region,
    apiVersion: "latest",
  });
}

async function getBucketNames() {
  const { Buckets } = await client.send(new ListBucketsCommand({}));
  return Buckets.map((bucket) => bucket.Name);
}

async function getBucketObjects(bucketName) {
  const { Contents } = await client.send(
    new ListObjectsV2Command({ Bucket: bucketName })
  );
  return Contents.map((it) => it.Key);
}

async function getObjectContent(bucketName, objectKey) {
  const { Body } = await client.send(
    new GetObjectCommand({
      Bucket: bucketName,
      Key: objectKey,
    })
  );
  return await utils.streamToString(Body);
}

export default {
  initClient,
  getBucketNames,
  getBucketObjects,
  getObjectContent,
};