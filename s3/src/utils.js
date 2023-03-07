function allSecretsAndVariablesDefined() {
  try {
    $injector.secrets.forEach((secret) => $secrets.get(secret));
    $injector.variables.forEach((variable) => $env.get(variable));
  } catch (err) {
    return false;
  }
  return true;
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
  allSecretsAndVariablesDefined,
  streamToString,
};
