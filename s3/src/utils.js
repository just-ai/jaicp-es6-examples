function secretsNotDefined() {
  return (
    $secrets.get("awsAccessKeyId", null) === null ||
    $secrets.get("awsSecretAccessKey", null) === null
  );
}

function streamToString(stream) {
  return new Promise((resolve, reject) => {
    const chunks = [];
    stream.on("data", (chunk) => chunks.push(chunk));
    stream.on("error", reject);
    stream.on("end", () => resolve(Buffer.concat(chunks).toString("utf8")));
  });
}

export default {
  secretsNotDefined,
  streamToString,
};
